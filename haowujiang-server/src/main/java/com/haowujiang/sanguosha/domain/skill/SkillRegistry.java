package com.haowujiang.sanguosha.domain.skill;

import com.haowujiang.sanguosha.domain.enums.CardKind;
import com.haowujiang.sanguosha.domain.model.BattleActor;
import com.haowujiang.sanguosha.domain.model.BattleCard;
import com.haowujiang.sanguosha.domain.model.BattleContext;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 武将技能注册器 —— 技能系统的统一查询入口。
 * <p>
 * 负责回答两类问题：
 * <ul>
 *   <li><b>规则查询</b>：该武将能否将牌 X 当牌 Y 使用？能否无视出杀次数？</li>
 *   <li><b>技能归属</b>：该武将是否拥有技能 Z？（解析武将的逗号分隔技能名称字段）</li>
 * </ul>
 * </p>
 *
 * <p><b>技能名匹配机制：</b>武将的 skillName 字段以逗号分隔存储（如 "龙胆" 或 "咆哮,奸雄"），
 * 通过 {@link #hasSkill} 解析后与 Spring 容器中所有 Skill Bean 的 {@link Skill#name()} 做精确匹配。</p>
 */
@Component
@RequiredArgsConstructor
public class SkillRegistry {

    /**
     * 规则技能列表（龙胆、咆哮等），Spring 自动注入所有 RuleSkill Bean
     */
    private final List<RuleSkill> ruleSkills;

    /**
     * 触发技能列表（奸雄等），Spring 自动注入所有 TriggerSkill Bean。
     * 由 {@link BattleEventBus} 使用，不直接在 SkillRegistry 中查询。
     */
    private final List<TriggerSkill> triggerSkills;

    /**
     * 判断武将是否可以将 card 当作 targetKind 使用。
     * 先检查牌本身是否就是 targetKind（无需技能），
     * 再遍历武将拥有的规则技查找卡牌转化能力。
     *
     * @param actor      使用牌的武将
     * @param card       手中的牌
     * @param targetKind 想要当作的牌类型
     * @return true 表示可以使用（直接匹配或通过技能转化）
     */
    public boolean canUseAs(BattleActor actor, BattleCard card, CardKind targetKind) {
        return card.getKind() == targetKind
                || findConversionSkill(actor, card, targetKind) != null;
    }

    /**
     * 在武将拥有的 RuleSkill 中查找能够把 card 转化成 targetKind 的技能。
     *
     * @param actor      使用牌的武将
     * @param card       手中的牌
     * @param targetKind 想要当作的牌类型
     * @return 匹配的 RuleSkill，未找到返回 null
     */
    public RuleSkill findConversionSkill(BattleActor actor, BattleCard card, CardKind targetKind) {
        // 牌本身就是目标类型 → 不需要技能转化
        if (card.getKind() == targetKind) {
            return null;
        }

        return ruleSkills.stream()
                .filter(skill -> hasSkill(actor, skill))
                .filter(skill -> skill.canUseAs(actor, card, targetKind))
                .findFirst()
                .orElse(null);
    }

    /**
     * 判断武将是否可以无视「杀」的使用次数限制。
     * <p>遍历武将拥有的所有规则技，任一返回 true 即可。</p>
     *
     * @param context 对战上下文
     * @param actor   使用牌的武将
     * @param card    手中的牌
     * @param useAs   实际当作的牌类型
     * @return true 表示可以继续出杀
     */
    public boolean ignoreShaLimit(BattleContext context,
                                  BattleActor actor,
                                  BattleCard card,
                                  CardKind useAs) {

        return ruleSkills.stream()
                .filter(skill -> hasSkill(actor, skill))
                .anyMatch(skill -> skill.ignoreShaLimit(context, actor, card, useAs));
    }

    /**
     * 判断武将是否拥有指定技能。
     * <p>
     * 武将的技能名称以逗号分隔存储在 {@link BattleActor#getSkillName()} 中（如 "龙胆" 或 "咆哮,奸雄"），
     * 此处解析后与 skill.name() 做精确匹配。
     * </p>
     *
     * @param actor 武将
     * @param skill 技能
     * @return true 表示该武将拥有此技能
     */
    public boolean hasSkill(BattleActor actor, Skill skill) {
        if (actor == null || actor.getSkillName() == null) {
            return false;
        }

        return Arrays.stream(actor.getSkillName().split("[,，]"))
                .map(String::trim)
                .anyMatch(name -> name.equals(skill.name()));
    }
}
