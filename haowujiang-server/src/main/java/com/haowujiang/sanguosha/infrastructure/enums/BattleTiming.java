package com.haowujiang.sanguosha.infrastructure.enums;

/**
 * 战斗时机窗口 —— 引擎内部的「钩子点」。
 * <p>
 * 时机窗口和 {@link PlayerAction 玩家操作} 是两个不同层次的概念：
 * <ul>
 *   <li><b>PlayerAction</b>：外部指令，由前端 / AI 发出（出牌、结束回合、响应等）</li>
 *   <li><b>BattleTiming</b>：引擎内部钩子，在游戏逻辑执行到特定节点时自动发布事件</li>
 * </ul>
 * 一个 PlayerAction 的执行过程会跨越多个 BattleTiming 窗口。
 * </p>
 *
 * <p>
 * {@link com.haowujiang.sanguosha.domain.skill.TriggerSkill 触发技} 通过实现
 * {@code supportTimings()} 声明自己关心哪些时机窗口，
 * {@link com.haowujiang.sanguosha.domain.skill.BattleEventBus 事件总线} 在 publish 时
 * 会把事件分发给匹配的触发技。
 * </p>
 */
public enum BattleTiming {

    // ========================
    // 卡牌使用相关
    // ========================

    /**
     * 检查是否允许使用该牌 —— 出牌合法性校验点。
     * <p>当前未发布，预留给后续的禁止出牌类技能（如「谦逊」不能被顺手牵羊等）。</p>
     */
    CARD_USE_CHECK,

    /**
     * 卡牌已使用 —— 牌已打出但目标尚未受到效果。
     * <p>当前在 useSha / useTao / useJiu / respondToPendingAttack 中发布。</p>
     */
    CARD_USED,

    /**
     * 已确认目标 —— 牌的目标已锁定。
     * <p>当前未发布，预留给目标转移类技能（如「流离」转移杀的目标）。</p>
     */
    CARD_TARGET_CONFIRMED,

    // ========================
    // 伤害相关
    // ========================

    /**
     * 伤害结算前 —— 即将扣减体力，此时可以修改伤害值或防止伤害。
     * <p>当前在 applyDamage() 中发布。</p>
     * <p>适合技能：防止伤害（如「谦逊」）、伤害+1（如「裸衣」）、转移伤害。</p>
     */
    DAMAGE_BEFORE,

    /**
     * 伤害结算后 —— 体力已扣减，适合受伤后触发的技能。
     * <p>当前在 applyDamage() 中发布，奸雄技能监听此窗口。</p>
     * <p>适合技能：受伤摸牌（「遗计」）、受伤获得牌（「奸雄」）、受伤判定（「刚烈」）。</p>
     */
    DAMAGE_AFTER,

    // ========================
    // 回合相关
    // ========================

    /**
     * 回合开始 —— 新回合开始，shaUsed 已重置、已摸牌。
     * <p>当前在 startTurn() 中发布。</p>
     * <p>适合技能：回合开始判定（「洛神」）、回合开始摸牌（「英姿」）、跳过判定阶段。</p>
     */
    TURN_START,

    /**
     * 回合结束 —— 当前回合即将结束，清理阶段。
     * <p>当前在 advanceStateMachine() 中发布。</p>
     * <p>适合技能：回合结束弃牌判定（「刚烈」）、回合结束摸牌（「闭月」）。</p>
     */
    TURN_END,

    // ========================
    // 以下预留给后续扩展
    // ========================

    // 摸牌阶段前 —— 可修改摸牌数量（「突袭」少摸一张、「裸衣」多摸一张）
    // DRAW_PHASE_BEFORE,

    // 摸牌阶段后 —— 摸牌完成后触发（「集智」摸牌后判定）
    // DRAW_PHASE_AFTER,

    // 弃牌阶段 —— 回合结束时的弃牌判定
    // DISCARD_PHASE,

    // 死亡结算前 —— 体力归零但尚未死亡（「不屈」翻判定牌求生）
    // BEFORE_DEATH,

    // 死亡结算后 —— 已确认死亡（「行殇」获得死者手牌）
    // AFTER_DEATH,
}