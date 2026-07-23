package com.haowujiang.sanguosha.domain.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haowujiang.sanguosha.infrastructure.enums.BattleLogType;
import com.haowujiang.sanguosha.infrastructure.enums.BattleStatus;
import com.haowujiang.sanguosha.infrastructure.enums.BattleTiming;
import com.haowujiang.sanguosha.infrastructure.enums.CardKind;
import com.haowujiang.sanguosha.infrastructure.enums.PlayerAction;
import com.haowujiang.sanguosha.infrastructure.enums.Side;
import com.haowujiang.sanguosha.infrastructure.enums.TurnPhase;
import com.haowujiang.sanguosha.domain.exception.BusinessException;
import com.haowujiang.sanguosha.domain.model.BattleActor;
import com.haowujiang.sanguosha.domain.model.BattleAiDecision;
import com.haowujiang.sanguosha.domain.model.BattleCard;
import com.haowujiang.sanguosha.domain.model.BattleEvent;
import com.haowujiang.sanguosha.domain.model.BattleLog;
import com.haowujiang.sanguosha.domain.model.BattleState;
import com.haowujiang.sanguosha.domain.service.AiDecisionDomainService;
import com.haowujiang.sanguosha.domain.service.BattleCardDomainService;
import com.haowujiang.sanguosha.domain.service.BattleDomainService;
import com.haowujiang.sanguosha.domain.service.GeneralDomainService;
import com.haowujiang.sanguosha.domain.skill.BattleEventBus;
import com.haowujiang.sanguosha.infrastructure.persistence.mapper.BattleRecordMapper;
import com.haowujiang.sanguosha.infrastructure.persistence.po.BattleRecord;
import com.haowujiang.sanguosha.infrastructure.persistence.po.General;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 对战领域服务实现 —— 核心编排层。
 *
 * <h3>架构设计</h3>
 * <p>
 * 使用 <b>TurnPhase 状态机</b> 显式管理回合流转，消除之前的方法链调用：
 * </p>
 * <pre>
 *   PLAYER_ACTION ──(END_TURN)──→ ENEMY_ACTION
 *       │                              │
 *       │ (出杀 → 等待响应)             │ (AI 出杀 → 等待响应)
 *       ↓                              ↓
 *   PLAYER_RESPONDING ←────────── ENEMY 攻击玩家
 *       │
 *       │ (出闪 / 不出)
 *       ↓
 *   advanceStateMachine() → 自动推进到下一个阶段
 * </pre>
 *
 * <h3>PlayerAction vs BattleTiming</h3>
 * <p>
 * {@link PlayerAction} 是外部指令（前端 / AI 发来的操作），
 * {@link BattleTiming} 是引擎内部钩子（技能系统的事件点）。
 * 一个 PlayerAction 的执行会在多个 BattleTiming 点发布事件。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class BattleDomainServiceImpl implements BattleDomainService {

    // ========================
    // 常量
    // ========================

    /** 开局手牌数 */
    private static final int OPENING_HAND_SIZE = 4;

    /** 每回合摸牌数 */
    private static final int TURN_DRAW_SIZE = 2;

    /** AI 每回合最多行动次数 */
    private static final int AI_MAX_ACTION_COUNT = 3;

    /** 牌的花色列表 */
    private static final List<String> SUITS = List.of("Spade", "Heart", "Club", "Diamond");

    /** 牌的点数列表 */
    private static final List<String> POINTS = List.of("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K");

    // ========================
    // 依赖注入
    // ========================

    private final BattleRecordMapper battleRecordMapper;
    private final GeneralDomainService generalDomainService;
    private final AiDecisionDomainService aiDecisionDomainService;
    private final BattleCardDomainService battleCardDomainService;
    private final BattleEventBus battleEventBus;
    private final ObjectMapper objectMapper;

    // ========================
    // 公共接口：创建对局
    // ========================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BattleState createBattle(Long userId, Long playerGeneralId, Long enemyGeneralId) {
        General playerGeneral = generalDomainService.getGeneralById(playerGeneralId);
        General enemyGeneral = generalDomainService.getGeneralById(enemyGeneralId);

        BattleState state = new BattleState(
                IdUtil.randomUUID(),
                new BattleActor(Side.PLAYER, playerGeneral),
                new BattleActor(Side.ENEMY, enemyGeneral),
                createDeck()
        );

        // 初始状态：玩家回合，玩家出牌阶段
        state.setCurrent(Side.PLAYER);
        state.setPhase(TurnPhase.PLAYER_ACTION);

        // 双方各摸 4 张起始手牌
        drawCards(state, state.getPlayer(), OPENING_HAND_SIZE);
        drawCards(state, state.getEnemy(), OPENING_HAND_SIZE);

        addLog(state, BattleLogType.SYSTEM, "对战开始，双方各摸四张牌");

        // 持久化
        BattleRecord record = new BattleRecord();
        record.setBattleNo(state.getId());
        record.setUserId(userId);
        record.setPlayerGeneralCode(playerGeneral.getCode());
        record.setEnemyGeneralCode(enemyGeneral.getCode());
        fillRecord(record, state);
        battleRecordMapper.insert(record);

        return state;
    }

    // ========================
    // 公共接口：查看对局
    // ========================

    @Override
    public BattleState getBattle(String battleId) {
        return readState(getBattleRecord(battleId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BattleState act(String battleId, PlayerAction action, String cardId) {
        BattleRecord record = getBattleRecord(battleId);
        BattleState state = readState(record);

        // 根据当前阶段分发处理
        dispatchByPhase(state, action, cardId);

        fillRecord(record, state);
        battleRecordMapper.updateById(record);

        return state;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BattleState runAiAction(String battleId) {
        BattleRecord record = getBattleRecord(battleId);
        BattleState state = readState(record);

        // 如果当前是玩家回合，先切换到 AI 回合
        if (state.getCurrent() != Side.ENEMY) {
            switchToEnemyTurn(state);
        }

        // AI 出牌
        runAiTurn(state);

        // 推进状态机（如果 AI 没有设置 pending response）
        advanceStateMachine(state);

        fillRecord(record, state);
        battleRecordMapper.updateById(record);

        return state;
    }

    // ========================
    // 核心：阶段分发（状态机）
    // ========================

    /**
     * 根据当前 TurnPhase 将玩家操作分发到对应的处理方法。
     * <p>
     * 这里是状态机的「心脏」—— 不再用 if-else 链判断 String action，
     * 而是先看当前阶段，再看操作类型是否合法。
     * </p>
     */
    private void dispatchByPhase(BattleState state, PlayerAction action, String cardId) {
        ensurePlayable(state);

        switch (state.getPhase()) {

            case PLAYER_ACTION -> {
                // 玩家出牌阶段：接受 PLAY_CARD 或 END_TURN
                if (action == null) {
                    action = PlayerAction.PLAY_CARD;
                }

                switch (action) {
                    case PLAY_CARD -> handlePlayerPlayCard(state, cardId);
                    case END_TURN -> handlePlayerEndTurn(state);
                    default -> throw new BusinessException(
                            "当前是出牌阶段，不支持操作: " + action.getLabel());
                }
            }

            case PLAYER_RESPONDING -> {
                // 玩家响应阶段：接受 RESPOND_CARD 或 PASS_RESPONSE
                if (action == null) {
                    action = PlayerAction.PASS_RESPONSE;
                }

                switch (action) {
                    case RESPOND_CARD, PLAY_CARD -> handlePlayerRespondCard(state, cardId);
                    case PASS_RESPONSE, END_TURN -> handlePlayerPassResponse(state);
                    default -> throw new BusinessException(
                            "当前需要响应，请选择出闪或放弃，不支持操作: " + action.getLabel());
                }
            }

            default -> throw new BusinessException(
                    "当前不是玩家操作阶段，当前阶段: " + state.getPhase().getLabel());
        }
    }

    /**
     * 玩家出牌 —— 打出手中一张牌
     */
    private void handlePlayerPlayCard(BattleState state, String cardId) {
        if (!StringUtils.hasText(cardId)) {
            throw new BusinessException("cardId 不能为空");
        }

        BattleCard card = findCard(state.getPlayer(), cardId);
        battleCardDomainService.useCard(state, state.getPlayer(), state.getEnemy(), card, false);

        // 出牌后可能需要等待响应（例如出杀后等对方出闪），或者直接推进状态机
        advanceStateMachine(state);
    }

    /**
     * 玩家结束回合 —— 切换到 AI 回合。
     */
    private void handlePlayerEndTurn(BattleState state) {
        addLog(state, BattleLogType.SYSTEM, "玩家结束回合，" + state.getEnemy().getName() + " 开始思考");

        switchToEnemyTurn(state);
        runAiTurn(state);
        advanceStateMachine(state);
    }

    // ========================
    // 响应阶段处理
    // ========================

    /**
     * 玩家选择出牌响应（例如被杀后出闪）
     */
    private void handlePlayerRespondCard(BattleState state, String cardId) {
        if (!StringUtils.hasText(cardId)) {
            throw new BusinessException("请选择用于响应的手牌");
        }

        BattleCard card = findCard(state.getPlayer(), cardId);
        battleCardDomainService.respondToPendingAttack(state, state.getPlayer(), card);

        // 响应完成，推进状态机
        advanceStateMachine(state);
    }

    /**
     * 玩家选择放弃响应（例如被杀后不出闪，硬吃伤害）。
     */
    private void handlePlayerPassResponse(BattleState state) {
        addLog(state, BattleLogType.PLAYER, state.getPlayer().getName() + " 选择不出闪");
        battleCardDomainService.passPendingAttack(state);

        // 响应完成（伤害已结算），推进状态机
        advanceStateMachine(state);
    }

    // ========================
    // 状态机推进（核心）
    // ========================

    /**
     * 显式推进状态机 —— 根据当前状态和 pendingResponse 决定下一个阶段。
     *
     * <p><b>这是整个设计的关键改进：状态转换不再藏在方法调用链里，而是在一个方法里显式声明。</b></p>
     *
     * <pre>
     *   FINISHED / pendingResponse != null → 终止推进（等玩家响应或已结束）
     *   PLAYER_ACTION → ENEMY_ACTION（如果 current == ENEMY）
     *   PLAYER_RESPONDING 结束后 → 回到 ENEMY_ACTION（继续 AI 回合）
     *   ENEMY_ACTION → TURN_END → PLAYER_ACTION（新回合）
     * </pre>
     */
    private void advanceStateMachine(BattleState state) {
        // 对战已结束 → 不再推进
        if (state.getWinner() != null) {
            state.setPhase(TurnPhase.FINISHED);
            return;
        }

        // 有等待响应 → 暂停推进，等玩家操作
        if (state.getPendingResponse() != null) {
            return;
        }

        // 当前是 AI 回合 → 继续 AI 出牌 或 切换到玩家回合
        if (state.getCurrent() == Side.ENEMY) {
            if (state.getPhase() == TurnPhase.PLAYER_RESPONDING) {
                // 玩家响应完毕，回到 AI 继续出牌
                state.setPhase(TurnPhase.ENEMY_ACTION);
                runAiTurn(state);
                // 递归检查（AI 出牌后可能又设了 pendingResponse 或结束对战）
                advanceStateMachine(state);
            } else {
                // AI 回合结束 → 结算回合结束 → 新回合开始
                endCurrentTurn(state);
            }
        }
    }

    /**
     * 结束当前回合，进入新回合。
     * <p>发布 TURN_END 事件（回合结束技能如「闭月」可在此触发），
     * 然后回合数 +1，切换到对方，发布 TURN_START 事件。</p>
     */
    private void endCurrentTurn(BattleState state) {
        // 1. 发布回合结束事件（之前从未发布！）
        BattleActor currentActor = state.getCurrent() == Side.PLAYER
                ? state.getPlayer() : state.getEnemy();
        battleEventBus.publish(
                state,
                new BattleEvent(BattleTiming.TURN_END, currentActor, null, null)
        );

        // 2. 推进回合数，切换行动方
        state.setRound(state.getRound() + 1);
        Side nextSide = state.getCurrent() == Side.PLAYER ? Side.ENEMY : Side.PLAYER;
        state.setCurrent(nextSide);

        // 3. 开始新回合
        BattleActor nextActor = nextSide == Side.PLAYER ? state.getPlayer() : state.getEnemy();
        startTurn(state, nextActor);

        // 4. 设置阶段
        if (nextSide == Side.PLAYER) {
            state.setPhase(TurnPhase.PLAYER_ACTION);
        } else {
            state.setPhase(TurnPhase.ENEMY_ACTION);
            // AI 回合自动执行
            runAiTurn(state);
            advanceStateMachine(state);
        }
    }

    /**
     * 切换到 AI 回合 —— 如有必要先结算当前回合结束。
     */
    private void switchToEnemyTurn(BattleState state) {
        // 当前是玩家回合，先触发回合结束
        battleEventBus.publish(
                state,
                new BattleEvent(BattleTiming.TURN_END, state.getPlayer(), null, null)
        );

        state.setCurrent(Side.ENEMY);
        state.setPhase(TurnPhase.ENEMY_ACTION);

        startTurn(state, state.getEnemy());
    }

    // ========================
    // 回合开始
    // ========================

    /**
     * 开始一个回合：重置出杀次数和酒状态，摸牌，发布 TURN_START 事件。
     */
    private void startTurn(BattleState state, BattleActor actor) {
        actor.setShaUsed(0);
        actor.setDrunk(false);

        drawCards(state, actor, TURN_DRAW_SIZE);

        addLog(state, logTypeForSide(actor.getSide()),
                actor.getName() + " 摸" + TURN_DRAW_SIZE + "张牌");

        // 发布回合开始事件（技能如「洛神」、「英姿」可监听此窗口）
        battleEventBus.publish(
                state,
                new BattleEvent(BattleTiming.TURN_START, actor, null, null)
        );
    }

    // ========================
    // AI 回合
    // ========================

    /**
     * AI 自动出牌 —— 最多尝试 AI_MAX_ACTION_COUNT 次。
     * <p>如果 AI 出杀且目标为玩家，会设置 pendingResponse 并暂停 AI 回合。</p>
     */
    private void runAiTurn(BattleState state) {
        List<String> thoughts = new ArrayList<>();

        for (int index = 0; index < AI_MAX_ACTION_COUNT; index++) {
            // 对战结束或有等待响应 → 停止
            if (state.getWinner() != null || state.getPendingResponse() != null) {
                break;
            }

            BattleAiDecision decision = aiDecisionDomainService.decide(state);
            thoughts.addAll(decision.getThoughts());

            if (StringUtils.hasText(decision.getReason())) {
                thoughts.add("AI 决策：" + decision.getReason());
            }

            // AI 选择结束回合
            if (decision.getAction() == PlayerAction.END_TURN) {
                addLog(state, BattleLogType.AI,
                        state.getEnemy().getName() + " 结束出牌阶段");
                break;
            }

            // AI 出牌但没有选牌
            if (!StringUtils.hasText(decision.getCardId())) {
                addLog(state, BattleLogType.AI,
                        state.getEnemy().getName() + " 没有选择手牌，结束出牌阶段");
                break;
            }

            // AI 出牌
            BattleCard card = findCard(state.getEnemy(), decision.getCardId());
            battleCardDomainService.useCard(state, state.getEnemy(), state.getPlayer(), card, true);

            // AI 出杀后需要等玩家响应 → 暂停 AI 回合
            if (state.getPendingResponse() != null) {
                thoughts.add("AI 已出杀，等待玩家选择是否出闪");
                state.setPhase(TurnPhase.PLAYER_RESPONDING);
                break;
            }
        }

        state.setAiThoughts(thoughts);
    }

    // ========================
    // 牌堆和摸牌
    // ========================

    /**
     * 从牌堆顶摸 count 张牌。
     * <p>牌堆不够时自动将弃牌堆洗回牌堆。</p>
     */
    private void drawCards(BattleState state, BattleActor actor, int count) {
        for (int index = 0; index < count; index++) {
            if (state.getDeck().isEmpty()) {
                washDiscardIntoDeck(state);
            }

            if (state.getDeck().isEmpty()) {
                return;
            }

            actor.getHand().add(state.getDeck().remove(state.getDeck().size() - 1));
        }
    }

    /**
     * 弃牌堆洗回牌堆（牌堆空时触发）。
     */
    private void washDiscardIntoDeck(BattleState state) {
        state.getDeck().addAll(state.getDiscard());
        state.getDiscard().clear();
        Collections.shuffle(state.getDeck());

        addLog(state, BattleLogType.SYSTEM, "牌堆已空，弃牌堆洗回牌堆");
    }

    // ========================
    // 牌堆构建
    // ========================

    /**
     * 构建初始牌堆：8 杀 6 闪 3 桃 3 酒 = 20 张。
     */
    private List<BattleCard> createDeck() {
        List<BattleCard> deck = new ArrayList<>();

        addCards(deck, CardKind.SHA, 8);
        addCards(deck, CardKind.SHAN, 6);
        addCards(deck, CardKind.TAO, 3);
        addCards(deck, CardKind.JIU, 3);

        Collections.shuffle(deck);
        return deck;
    }

    private void addCards(List<BattleCard> deck, CardKind kind, int count) {
        for (int index = 0; index < count; index++) {
            int cardIndex = deck.size();

            deck.add(new BattleCard(
                    IdUtil.randomUUID(),
                    kind,
                    SUITS.get(cardIndex % SUITS.size()),
                    POINTS.get(cardIndex % POINTS.size())
            ));
        }
    }

    // ========================
    // 工具方法
    // ========================

    /**
     * 从武将手牌中查找指定 ID 的牌，找不到抛异常。
     */
    private BattleCard findCard(BattleActor actor, String cardId) {
        return actor.getHand().stream()
                .filter(card -> card.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("手牌中不存在该牌：" + cardId));
    }

    /**
     * 确保对战还可以继续（未结束），否则抛异常。
     */
    private void ensurePlayable(BattleState state) {
        if (state.getWinner() != null) {
            throw new BusinessException("对战已经结束，胜利方：" + state.getWinner());
        }
    }

    // ========================
    // 持久化辅助
    // ========================

    private BattleRecord getBattleRecord(String battleNo) {
        LambdaQueryWrapper<BattleRecord> wrapper = Wrappers.lambdaQuery(BattleRecord.class)
                .eq(BattleRecord::getDeleted, 0)
                .eq(BattleRecord::getBattleNo, battleNo);

        BattleRecord record = battleRecordMapper.selectOne(wrapper);

        if (record == null) {
            throw new BusinessException("对战不存在");
        }

        return record;
    }

    private BattleState readState(BattleRecord record) {
        try {
            return objectMapper.readValue(record.getStateJson(), BattleState.class);
        } catch (JsonProcessingException exception) {
            throw new BusinessException("对战快照解析失败");
        }
    }

    private void fillRecord(BattleRecord record, BattleState state) {
        record.setRound(state.getRound());
        record.setCurrentSide(state.getCurrent().getValue());
        record.setPlayerHp(state.getPlayer().getHp());
        record.setEnemyHp(state.getEnemy().getHp());
        record.setWinner(state.getWinner() != null ? state.getWinner().getValue() : null);
        record.setStatus(state.getWinner() == null ? BattleStatus.PLAYING : BattleStatus.FINISHED);
        record.setStateJson(toJson(state));
    }

    private String toJson(BattleState state) {
        try {
            return objectMapper.writeValueAsString(state);
        } catch (JsonProcessingException exception) {
            throw new BusinessException("对战快照保存失败");
        }
    }

    private void addLog(BattleState state, BattleLogType type, String text) {
        state.getLogs().add(0, new BattleLog(
                "log-" + IdUtil.randomUUID(),
                type,
                text
        ));
    }

    private static BattleLogType logTypeForSide(Side side) {
        return side == Side.PLAYER ? BattleLogType.PLAYER : BattleLogType.ENEMY;
    }
}
