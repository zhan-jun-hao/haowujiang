package com.haowujiang.sanguosha.domain.model;

import com.haowujiang.sanguosha.domain.enums.PendingResponseType;
import com.haowujiang.sanguosha.domain.enums.Side;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对战中等待响应的状态 —— 当一方出杀后，另一方需要选择出闪或不出闪。
 * <p>
 * 当前主要用于 AI 出杀后等待玩家响应（出闪）。玩家出杀对 AI 时，
 * AI 的响应在 {@code resolveAttack} 中直接结算，不走 pending 机制。
 * </p>
 *
 * <p>pendingResponse 不为 null 时，状态机暂停在 PLAYER_RESPONDING 阶段，
 * 等待玩家通过 {@code RESPOND_CARD} 或 {@code PASS_RESPONSE} 操作响应。</p>
 */
@Data
@NoArgsConstructor
public class BattlePendingResponse {

    /**
     * 响应类型（当前仅 SHA 在使用中）
     */
    private PendingResponseType type;

    /**
     * 发起方阵营（出杀的一方）
     */
    private Side attackerSide;

    /**
     * 响应方阵营（需要出闪的一方）
     */
    private Side defenderSide;

    /**
     * 发起方武将名称
     */
    private String attackerName;

    /**
     * 响应方武将名称
     */
    private String defenderName;

    /**
     * 本次攻击使用的牌
     */
    private BattleCard attackCard;

    /**
     * 如果响应方不出闪，将会受到的伤害值（默认 1，酒杀为 2）
     */
    private int damage;

    /**
     * 工厂方法：创建「需要出闪」的待响应状态。
     *
     * @param attacker 攻击方
     * @param defender 防御方
     * @param card     攻击用的牌
     * @param damage   不出闪时将受到的伤害
     * @return 待响应状态
     */
    public static BattlePendingResponse sha(BattleActor attacker,
                                            BattleActor defender,
                                            BattleCard card,
                                            int damage) {
        BattlePendingResponse response = new BattlePendingResponse();
        response.setType(PendingResponseType.SHA);
        response.setAttackerSide(attacker.getSide());
        response.setDefenderSide(defender.getSide());
        response.setAttackerName(attacker.getName());
        response.setDefenderName(defender.getName());
        response.setAttackCard(card);
        response.setDamage(damage);
        return response;
    }
}
