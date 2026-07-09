package com.haowujiang.sanguosha.domain.model;

import com.haowujiang.sanguosha.domain.enums.BattleTiming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对战事件领域模型 —— 在特定{@link BattleTiming 时机窗口}发布给技能系统的事件。
 * <p>
 * 由 Service 层在游戏关键节点创建，通过 {@link com.haowujiang.sanguosha.domain.skill.BattleEventBus 事件总线}
 * 分发给匹配的 {@link com.haowujiang.sanguosha.domain.skill.TriggerSkill 触发技}。
 * </p>
 *
 * <p>每个事件携带四个信息：</p>
 * <ul>
 *   <li><b>timing</b> —— 当前的时机窗口（CARD_USED / DAMAGE_BEFORE / DAMAGE_AFTER / TURN_START / TURN_END 等）</li>
 *   <li><b>actor</b> —— 当前操作者（出牌方 / 造成伤害方）</li>
 *   <li><b>target</b> —— 目标（被出牌方 / 受到伤害方，可能为 null 如使用桃）</li>
 *   <li><b>card</b> —— 使用的牌（可能为 null 如回合开始事件）</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BattleEvent {

    /**
     * 战斗时机窗口 —— 当前事件发生在哪个时机点
     */
    private BattleTiming timing;

    /**
     * 当前操作者（出牌方 / 造成伤害方）
     */
    private BattleActor actor;

    /**
     * 目标（被出牌方 / 受到伤害方），可能为 null
     */
    private BattleActor target;

    /**
     * 使用的牌，可能为 null（如回合开始 / 结束事件）
     */
    private BattleCard card;
}
