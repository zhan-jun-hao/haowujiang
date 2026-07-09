package com.haowujiang.sanguosha.domain.skill;

/**
 * 武将技能基础接口 —— 所有技能的根接口。
 * <p>
 * 技能分为两大类：
 * <ul>
 *   <li>{@link TriggerSkill 触发技} —— 在特定时机窗口自动触发（如「奸雄」受伤后获得牌）</li>
 *   <li>{@link RuleSkill 规则修改技} —— 修改游戏规则，不依赖时机窗口（如「龙胆」杀闪互转、「咆哮」无限出杀）</li>
 * </ul>
 * </p>
 */
public interface Skill {

    /**
     * 技能编码（唯一标识，如 "jian-xiong"）
     *
     * @return 技能编码
     */
    String code();

    /**
     * 技能中文名称（如 "奸雄"）
     *
     * @return 技能名称
     */
    String name();
}
