package com.haowujiang.sanguosha.domain.skill;

import com.haowujiang.sanguosha.infrastructure.enums.CardKind;
import com.haowujiang.sanguosha.domain.model.BattleActor;
import com.haowujiang.sanguosha.domain.model.BattleCard;
import com.haowujiang.sanguosha.domain.model.BattleContext;

/**
 * 规则修改技接口 —— 修改游戏规则，不依赖时机窗口。
 * <p>
 * 与 {@link TriggerSkill 触发技} 不同，规则技在出牌合法性判断时被 {@link SkillRegistry 技能注册器}
 * 主动调用，是一种「查询模式」而非「事件回调模式」。
 * </p>
 *
 * <p><b>示例：</b></p>
 * <pre>
 *   // 龙胆：杀当闪、闪当杀
 *   public class LongDanSkill implements RuleSkill {
 *       public boolean canUseAs(BattleActor actor, BattleCard card, CardKind targetKind) {
 *           return (card.getKind() == CardKind.SHA && targetKind == CardKind.SHAN)
 *                   || (card.getKind() == CardKind.SHAN && targetKind == CardKind.SHA);
 *       }
 *   }
 *
 *   // 咆哮：杀无次数限制
 *   public class PaoXiaoSkill implements RuleSkill {
 *       public boolean ignoreShaLimit(BattleContext context, BattleActor actor,
 *                                      BattleCard card, CardKind useAs) {
 *           return useAs == CardKind.SHA;
 *       }
 *   }
 * </pre>
 */
public interface RuleSkill extends Skill {

    /**
     * 是否允许将 card 当作 targetKind 使用（卡牌转化）。
     * <p>例如龙胆技能：手上的「杀」可以当「闪」打出，手上的「闪」可以当「杀」打出。</p>
     *
     * @param actor      使用牌的武将
     * @param card       手中的原牌
     * @param targetKind 想要当作的目标牌类型
     * @return true 表示允许转化使用
     */
    default boolean canUseAs(BattleActor actor, BattleCard card, CardKind targetKind) {
        return false;
    }

    /**
     * 是否忽略「杀」的使用次数限制。
     * <p>例如咆哮技能：本回合无论已出多少张杀，都可以继续出。</p>
     *
     * @param context 对战上下文
     * @param actor   使用牌的武将
     * @param card    手中的牌
     * @param useAs   实际当作的牌类型
     * @return true 表示无视次数限制
     */
    default boolean ignoreShaLimit(BattleContext context, BattleActor actor, BattleCard card, CardKind useAs) {
        return false;
    }
}
