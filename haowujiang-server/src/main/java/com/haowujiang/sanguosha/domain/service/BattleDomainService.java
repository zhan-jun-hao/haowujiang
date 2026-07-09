package com.haowujiang.sanguosha.domain.service;

import com.haowujiang.sanguosha.domain.enums.PlayerAction;
import com.haowujiang.sanguosha.domain.model.BattleState;

/**
 * 对战领域服务 —— 对战的核心编排接口。
 * <p>
 * 玩家的操作通过 {@link PlayerAction} 枚举传入，内部通过 {@code TurnPhase} 状态机分发，
 * 不再是之前的 String + if-else 方法链。
 * </p>
 */
public interface BattleDomainService {

    /**
     * 创建对局
     *
     * @param userId           用户 ID
     * @param playerGeneralId  玩家武将 ID
     * @param enemyGeneralId   敌方武将 ID
     * @return 初始化的对战状态
     */
    BattleState createBattle(Long userId, Long playerGeneralId, Long enemyGeneralId);

    /**
     * 查看对局
     *
     * @param battleId 对战编号
     * @return 当前对战状态
     */
    BattleState getBattle(String battleId);

    /**
     * 玩家行动 —— 出牌、结束回合、响应出牌等。
     * <p>
     * 内部根据 {@code BattleState.phase} 做 switch 分发：
     * <ul>
     *   <li>PLAYER_ACTION → 接受 PLAY_CARD / END_TURN</li>
     *   <li>PLAYER_RESPONDING → 接受 RESPOND_CARD / PASS_RESPONSE</li>
     * </ul>
     * </p>
     *
     * @param battleId 对战编号
     * @param action   玩家操作类型
     * @param cardId   操作关联的手牌 ID（出牌 / 响应时必填）
     * @return 更新后的对战状态
     */
    BattleState act(String battleId, PlayerAction action, String cardId);

    /**
     * 触发 AI 出牌
     *
     * @param battleId 对战编号
     * @return 更新后的对战状态
     */
    BattleState runAiAction(String battleId);
}
