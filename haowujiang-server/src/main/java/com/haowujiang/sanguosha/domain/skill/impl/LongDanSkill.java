package com.haowujiang.sanguosha.domain.skill.impl;

import com.haowujiang.sanguosha.domain.enums.CardKind;
import com.haowujiang.sanguosha.domain.model.BattleActor;
import com.haowujiang.sanguosha.domain.model.BattleCard;
import com.haowujiang.sanguosha.domain.skill.RuleSkill;
import org.springframework.stereotype.Component;

/**
 * 龙胆：杀  闪 转化
 */
@Component
public class LongDanSkill implements RuleSkill {

    @Override
    public String code() {
        return "long-dan";
    }

    @Override
    public String name() {
        return "龙胆";
    }

    @Override
    public boolean canUseAs(BattleActor actor, BattleCard card, CardKind targetKind) {
        return (card.getKind() == CardKind.SHA && targetKind == CardKind.SHAN)
                || (card.getKind() == CardKind.SHAN && targetKind == CardKind.SHA);
    }
}