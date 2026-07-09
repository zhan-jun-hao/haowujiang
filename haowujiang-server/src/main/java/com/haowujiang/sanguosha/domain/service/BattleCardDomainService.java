package com.haowujiang.sanguosha.domain.service;

import com.haowujiang.sanguosha.domain.enums.CardKind;
import com.haowujiang.sanguosha.domain.model.BattleActor;
import com.haowujiang.sanguosha.domain.model.BattleCard;
import com.haowujiang.sanguosha.domain.model.BattleState;

/**
 * 牌操作接口
 */
public interface BattleCardDomainService {

    BattleCard findFirstCard(BattleActor actor, CardKind kind);

    BattleCard findAttackCard(BattleState state, BattleActor actor);

    boolean canUseAs(BattleState state, BattleActor actor, BattleCard card, CardKind targetKind);

    boolean canUseSha(BattleState state, BattleActor actor, BattleCard card);

    void useCard(BattleState state, BattleActor actor, BattleActor target, BattleCard card, boolean aiControlled);

    void respondToPendingAttack(BattleState state, BattleActor defender, BattleCard card);

    void passPendingAttack(BattleState state);
}
