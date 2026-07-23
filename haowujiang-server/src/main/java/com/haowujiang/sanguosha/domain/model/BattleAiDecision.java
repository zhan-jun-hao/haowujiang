package com.haowujiang.sanguosha.domain.model;

import com.haowujiang.sanguosha.infrastructure.enums.PlayerAction;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 对战决策领域模型 —— AI 给系统看的决策对象。
 * <p>
 * 使用 {@link PlayerAction} 枚举而不是 String 常量，
 * 与玩家操作使用同一套类型系统，保证类型安全。
 * </p>
 */
@Data
@NoArgsConstructor
public class BattleAiDecision {

    /**
     * AI 决定的操作类型（出牌 / 结束回合）
     */
    private PlayerAction action = PlayerAction.END_TURN;

    /**
     * 选择使用的手牌 ID（出牌时必填）
     */
    private String cardId;

    /**
     * 决策原因（用于日志展示）
     */
    private String reason;

    /**
     * AI 思考过程列表（用于前端展示）
     */
    private List<String> thoughts = new ArrayList<>();

    /**
     * 工厂方法：创建「出牌」决策
     *
     * @param cardId   要打出的手牌 ID
     * @param reason   决策原因
     * @param thoughts AI 思考过程
     * @return 出牌决策
     */
    public static BattleAiDecision playCard(String cardId, String reason, List<String> thoughts) {
        BattleAiDecision decision = new BattleAiDecision();
        decision.setAction(PlayerAction.PLAY_CARD);
        decision.setCardId(cardId);
        decision.setReason(reason);
        decision.setThoughts(thoughts);
        return decision;
    }

    /**
     * 工厂方法：创建「结束回合」决策
     *
     * @param reason   决策原因
     * @param thoughts AI 思考过程
     * @return 结束回合决策
     */
    public static BattleAiDecision endTurn(String reason, List<String> thoughts) {
        BattleAiDecision decision = new BattleAiDecision();
        decision.setAction(PlayerAction.END_TURN);
        decision.setReason(reason);
        decision.setThoughts(thoughts);
        return decision;
    }
}
