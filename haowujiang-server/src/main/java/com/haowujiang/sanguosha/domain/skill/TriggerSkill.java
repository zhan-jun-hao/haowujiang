package com.haowujiang.sanguosha.domain.skill;

import com.haowujiang.sanguosha.infrastructure.enums.BattleTiming;
import com.haowujiang.sanguosha.domain.model.BattleContext;
import com.haowujiang.sanguosha.domain.model.BattleEvent;
import java.util.Set;

/**
 * 触发技接口 —— 在特定的{@link BattleTiming 时机窗口}自动触发。
 * <p>
 * 与 {@link RuleSkill 规则修改技} 不同，触发技不参与「能不能出牌」的判断，
 * 而是在引擎推进到某个时机点时被 {@link BattleEventBus 事件总线} 回调。
 * </p>
 *
 * <p><b>示例：</b></p>
 * <pre>
 *   // 奸雄：受到伤害后，获得造成伤害的牌
 *   public class JianXiongSkill implements TriggerSkill {
 *       public Set&lt;BattleTiming&gt; supportTimings() {
 *           return Set.of(BattleTiming.DAMAGE_AFTER);
 *       }
 *       public void onTrigger(BattleContext context, BattleEvent event) {
 *           context.obtainCard(event.getTarget(), event.getCard());
 *       }
 *   }
 * </pre>
 */
public interface TriggerSkill extends Skill {

    /**
     * 本技能关心的时机窗口集合。
     * <p>一个技能可以监听多个时机（例如既能在回合开始触发，也能在伤害后触发）。
     * 当 {@link BattleEventBus} 发布的 {@link BattleEvent} 的 timing 包含在这个集合中时，
     * 会回调 {@link #onTrigger}。</p>
     *
     * @return 非 null 的时机窗口集合，返回空集合表示不监听任何时机
     */
    Set<BattleTiming> supportTimings();

    /**
     * 响应战斗时机 —— 当匹配的时机窗口到来时被调用。
     * <p>
     * 实现方应在方法内自行判断 actor / target 是否拥有本技能
     * （EventBus 已经做过一次预筛选，但建议方法内再做一次安全检查）。
     * </p>
     *
     * @param context 对战上下文，可以从中获取 BattleState 以及调用辅助方法
     * @param event   触发本次回调的战斗事件，包含 timing、actor、target、card
     */
    void onTrigger(BattleContext context, BattleEvent event);
}
