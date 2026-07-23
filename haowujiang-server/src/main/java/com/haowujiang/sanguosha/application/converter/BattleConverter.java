package com.haowujiang.sanguosha.application.converter;

import com.haowujiang.sanguosha.interfaces.vo.battle.response.BattleActorRespVo;
import com.haowujiang.sanguosha.interfaces.vo.battle.response.BattleCardRespVo;
import com.haowujiang.sanguosha.interfaces.vo.battle.response.BattleLogRespVo;
import com.haowujiang.sanguosha.interfaces.vo.battle.response.BattlePendingResponseRespVo;
import com.haowujiang.sanguosha.interfaces.vo.battle.response.BattleRespVo;
import com.haowujiang.sanguosha.domain.model.BattleActor;
import com.haowujiang.sanguosha.domain.model.BattleCard;
import com.haowujiang.sanguosha.domain.model.BattleLog;
import com.haowujiang.sanguosha.domain.model.BattlePendingResponse;
import com.haowujiang.sanguosha.domain.model.BattleState;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BattleConverter {

    BattleConverter INSTANCE = Mappers.getMapper(BattleConverter.class);

    default BattleRespVo modelToVo(BattleState state) {
        if (state == null) {
            return null;
        }
        BattleRespVo vo = new BattleRespVo();
        vo.setId(state.getId());
        vo.setRound(state.getRound());
        vo.setCurrent(state.getCurrent().getValue());
        vo.setPlayer(actorToVo(state.getPlayer(), true));
        vo.setEnemy(actorToVo(state.getEnemy(), false));
        vo.setDeckCount(state.getDeck().size());
        vo.setDiscardCount(state.getDiscard().size());
        vo.setLogs(state.getLogs().stream()
                .map(this::logToVo)
                .collect(Collectors.toCollection(ArrayList::new)));
        vo.setAiThoughts(new ArrayList<>(state.getAiThoughts()));
        vo.setPendingResponse(pendingResponseToVo(state.getPendingResponse()));
        vo.setWinner(state.getWinner() != null ? state.getWinner().getValue() : null);
        return vo;
    }

    default BattleActorRespVo actorToVo(BattleActor actor, boolean exposeHand) {
        if (actor == null) {
            return null;
        }
        List<BattleCardRespVo> hand = exposeHand
                ? actor.getHand().stream()
                .map(this::cardToVo)
                .collect(Collectors.toCollection(ArrayList::new))
                : new ArrayList<>();
        BattleActorRespVo vo = new BattleActorRespVo();
        vo.setSide(actor.getSide().getValue());
        vo.setGeneralId(actor.getGeneralId());
        vo.setName(actor.getName());
        vo.setSkillName(actor.getSkillName());
        vo.setHp(actor.getHp());
        vo.setMaxHp(actor.getMaxHp());
        vo.setHand(hand);
        vo.setHandCount(actor.getHand().size());
        vo.setDrunk(actor.isDrunk());
        vo.setShaUsed(actor.getShaUsed());
        vo.setDefeated(actor.isDefeated());
        return vo;
    }

    default BattleCardRespVo cardToVo(BattleCard card) {
        if (card == null) {
            return null;
        }
        BattleCardRespVo vo = new BattleCardRespVo();
        vo.setId(card.getId());
        vo.setKind(card.getKind().name().toLowerCase());
        vo.setName(card.getName());
        vo.setSuit(card.getSuit());
        vo.setPoint(card.getPoint());
        return vo;
    }

    default BattleLogRespVo logToVo(BattleLog log) {
        if (log == null) {
            return null;
        }
        BattleLogRespVo vo = new BattleLogRespVo();
        vo.setId(log.getId());
        vo.setType(log.getType().getValue());
        vo.setText(log.getText());
        return vo;
    }

    default BattlePendingResponseRespVo pendingResponseToVo(BattlePendingResponse pendingResponse) {
        if (pendingResponse == null) {
            return null;
        }
        BattlePendingResponseRespVo vo = new BattlePendingResponseRespVo();
        vo.setType(pendingResponse.getType().getValue());
        vo.setAttackerSide(pendingResponse.getAttackerSide().getValue());
        vo.setDefenderSide(pendingResponse.getDefenderSide().getValue());
        vo.setAttackerName(pendingResponse.getAttackerName());
        vo.setDefenderName(pendingResponse.getDefenderName());
        BattleCard attackCard = pendingResponse.getAttackCard();
        if (attackCard != null) {
            vo.setAttackCardId(attackCard.getId());
            vo.setAttackCardName(attackCard.getName());
            vo.setAttackCardSuit(attackCard.getSuit());
            vo.setAttackCardPoint(attackCard.getPoint());
        }
        vo.setDamage(pendingResponse.getDamage());
        return vo;
    }
}
