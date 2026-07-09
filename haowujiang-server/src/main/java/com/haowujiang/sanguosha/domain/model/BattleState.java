package com.haowujiang.sanguosha.domain.model;

import com.haowujiang.sanguosha.domain.enums.Side;
import com.haowujiang.sanguosha.domain.enums.TurnPhase;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对战状态领域模型 —— 整个对战的完整快照。
 * <p>
 * 通过 {@link #phase} 字段显式声明当前处于哪个阶段，
 * Service 层根据阶段做 switch 分发，而不是通过方法调用链隐式推进状态。
 * </p>
 */
@Data
@NoArgsConstructor
public class BattleState {

    /**
     * 对战编号
     */
    private String id;

    /**
     * 当前回合数
     */
    private int round = 1;

    /**
     * 当前行动方（谁的回合）
     */
    private Side current;

    /**
     * 当前回合阶段 —— 显式状态机，决定 Service 层当前应该接受什么操作。
     *
     * <pre>
     *   PLAYER_ACTION → 接受 PLAY_CARD / END_TURN
     *   PLAYER_RESPONDING → 接受 RESPOND_CARD / PASS_RESPONSE
     *   ENEMY_ACTION → AI 自动出牌（不接受玩家输入）
     *   FINISHED → 对战结束，拒绝一切操作
     * </pre>
     */
    private TurnPhase phase = TurnPhase.PLAYER_ACTION;

    /**
     * 玩家角色
     */
    private BattleActor player;

    /**
     * 敌方角色
     */
    private BattleActor enemy;

    /**
     * 当前牌堆
     */
    private List<BattleCard> deck = new ArrayList<>();

    /**
     * 当前弃牌堆
     */
    private List<BattleCard> discard = new ArrayList<>();

    /**
     * 对战日志列表
     */
    private List<BattleLog> logs = new ArrayList<>();

    /**
     * AI 思考过程列表
     */
    private List<String> aiThoughts = new ArrayList<>(List.of("Waiting for your enemy to read RAG memory."));

    /**
     * 当前等待响应的动作，为空表示无需响应
     */
    private BattlePendingResponse pendingResponse;

    /**
     * 胜利方，未结束时为空
     */
    private Side winner;

    public BattleState(String id, BattleActor player, BattleActor enemy, List<BattleCard> deck) {
        this.id = id;
        this.player = player;
        this.enemy = enemy;
        this.deck = deck == null ? new ArrayList<>() : new ArrayList<>(deck);
    }
}
