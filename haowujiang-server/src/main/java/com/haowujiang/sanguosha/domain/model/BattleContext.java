package com.haowujiang.sanguosha.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对战技能上下文 —— 包装 {@link BattleState}，提供给技能回调方法使用。
 * <p>
 * 除了可以直接访问对战状态外，还提供了一些辅助方法（如 {@link #obtainCard}），
 * 供技能在 onTrigger 中方便地修改游戏状态。
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BattleContext {

    /**
     * 当前对战状态的完整快照
     */
    private BattleState state;

    /**
     * 让指定武将获得一张牌（加入手牌）。
     * <p>典型场景：奸雄技能 —— 受到伤害后获得造成伤害的牌。</p>
     *
     * @param actor 获得牌的武将
     * @param card  被获得的牌
     */
    public void obtainCard(BattleActor actor, BattleCard card) {
        if (actor == null || card == null) {
            return;
        }
        actor.getHand().add(card);
    }
}
