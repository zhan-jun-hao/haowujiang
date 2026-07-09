package com.haowujiang.sanguosha.domain.skill.impl;

import com.haowujiang.sanguosha.domain.enums.BattleTiming;
import com.haowujiang.sanguosha.domain.model.BattleActor;
import com.haowujiang.sanguosha.domain.model.BattleCard;
import com.haowujiang.sanguosha.domain.model.BattleContext;
import com.haowujiang.sanguosha.domain.model.BattleEvent;
import com.haowujiang.sanguosha.domain.skill.TriggerSkill;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * 奸雄：受到伤害后，获得造成伤害的牌。
 * <p>
 * 这是触发技的典型实现 —— 订阅 {@link BattleTiming#DAMAGE_AFTER 伤害结算后} 时机窗口，
 * 当持有该技能的武将受到伤害后，自动将造成伤害的牌收入手牌。
 * </p>
 */
@Component
public class JianXiongSkill implements TriggerSkill {

    @Override
    public String code() {
        return "jian-xiong";
    }

    @Override
    public String name() {
        return "奸雄";
    }

    /**
     * 监听伤害结算后的时机窗口。
     * 如果后续需要扩展到其他时机（例如也监听 DAMAGE_BEFORE），
     * 直接在 Set 里添加即可。
     */
    @Override
    public Set<BattleTiming> supportTimings() {
        return Set.of(BattleTiming.DAMAGE_AFTER);
    }

    /**
     * 伤害结算后触发：受伤方获得造成伤害的那张牌。
     *
     * @param context 对战上下文
     * @param event   战斗事件（包含伤害来源、受伤目标、造成伤害的牌）
     */
    @Override
    public void onTrigger(BattleContext context, BattleEvent event) {
        BattleActor target = event.getTarget();
        BattleCard card = event.getCard();

        if (target == null || card == null) {
            return;
        }

        // 将造成伤害的牌收入受伤方手牌
        context.obtainCard(target, card);
    }
}
