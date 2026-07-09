package com.haowujiang.sanguosha.domain.skill;

import com.haowujiang.sanguosha.domain.model.BattleContext;
import com.haowujiang.sanguosha.domain.model.BattleEvent;
import com.haowujiang.sanguosha.domain.model.BattleState;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 对战事件总线 —— 在游戏的各个{@link com.haowujiang.sanguosha.domain.enums.BattleTiming 时机窗口}
 * 发布 {@link BattleEvent}，分发给匹配的 {@link TriggerSkill 触发技}。
 *
 * <p><b>工作流程：</b></p>
 * <ol>
 *   <li>Service 层在关键节点创建 BattleEvent（携带 timing、actor、target、card）并调用 publish()</li>
 *   <li>EventBus 遍历所有注册的 TriggerSkill</li>
 *   <li>检查触发技的 supportTimings() 是否包含当前事件的 timing</li>
 *   <li>检查事件的 actor 或 target 是否拥有该技能</li>
 *   <li>满足条件则回调 triggerSkill.onTrigger(context, event)</li>
 * </ol>
 *
 * <p><b>设计说明：</b></p>
 * <ul>
 *   <li>目前是同步遍历所有触发技，未来技能数量增多时可改为按 timing 分组索引，提升性能</li>
 *   <li>onTrigger 按 Spring Bean 的注入顺序执行，多个技能监听同一 timing 时需要注意执行顺序</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class BattleEventBus {

    /**
     * Spring 自动注入的所有 TriggerSkill 实现
     */
    private final List<TriggerSkill> triggerSkills;

    /**
     * 技能注册器 —— 用于判断武将是否拥有某个技能
     */
    private final SkillRegistry skillRegistry;

    /**
     * 在指定时机窗口发布战斗事件。
     * <p>
     * 会遍历所有注册的触发技，筛选出：
     * <ul>
     *   <li>该技能的 supportTimings() 包含 event.timing</li>
     *   <li>event.actor 或 event.target 拥有该技能</li>
     * </ul>
     * 对满足条件的技能调用 onTrigger。
     * </p>
     *
     * @param state 当前对战状态
     * @param event 要发布的战斗事件
     */
    public void publish(BattleState state, BattleEvent event) {
        if (state == null || event == null || event.getTiming() == null) {
            return;
        }

        BattleContext context = new BattleContext(state);

        for (TriggerSkill triggerSkill : triggerSkills) {
            // 该技能订阅的时机集合不包含当前事件的时机 → 跳过
            if (triggerSkill.supportTimings() == null
                    || !triggerSkill.supportTimings().contains(event.getTiming())) {
                continue;
            }

            // 事件的参与方（操作者 / 目标）都没有这个技能 → 跳过
            if (!skillRegistry.hasSkill(event.getActor(), triggerSkill)
                    && !skillRegistry.hasSkill(event.getTarget(), triggerSkill)) {
                continue;
            }

            // 回调技能逻辑
            triggerSkill.onTrigger(context, event);
        }
    }
}
