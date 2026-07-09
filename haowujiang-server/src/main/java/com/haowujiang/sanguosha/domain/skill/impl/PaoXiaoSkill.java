package com.haowujiang.sanguosha.domain.skill.impl;

import com.haowujiang.sanguosha.domain.enums.CardKind;
import com.haowujiang.sanguosha.domain.model.BattleActor;
import com.haowujiang.sanguosha.domain.model.BattleCard;
import com.haowujiang.sanguosha.domain.model.BattleContext;
import com.haowujiang.sanguosha.domain.skill.RuleSkill;
import org.springframework.stereotype.Component;

/**
 * 咆哮：杀无次数限制
 */
@Component
public class PaoXiaoSkill implements RuleSkill {

    @Override
    public String code() {
        return "pao-xiao";
    }

    @Override
    public String name() {
        return "咆哮";
    }

    @Override
    public boolean ignoreShaLimit(BattleContext context, BattleActor actor, BattleCard card, CardKind useAs) {
        return useAs == CardKind.SHA;
    }
}