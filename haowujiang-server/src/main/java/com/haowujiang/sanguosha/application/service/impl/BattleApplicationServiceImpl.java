package com.haowujiang.sanguosha.application.service.impl;

import com.haowujiang.sanguosha.application.converter.BattleConverter;
import com.haowujiang.sanguosha.application.service.BattleApplicationService;
import com.haowujiang.sanguosha.application.vo.battle.response.BattleRespVo;
import com.haowujiang.sanguosha.domain.enums.PlayerAction;
import com.haowujiang.sanguosha.domain.service.BattleDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 对战应用服务实现 —— 薄薄一层，负责模型转换。
 */
@Service
@RequiredArgsConstructor
public class BattleApplicationServiceImpl implements BattleApplicationService {

    private final BattleDomainService battleDomainService;

    @Override
    public BattleRespVo createBattle(Long userId, Long playerGeneralId, Long enemyGeneralId) {
        return BattleConverter.INSTANCE.modelToVo(
                battleDomainService.createBattle(userId, playerGeneralId, enemyGeneralId));
    }

    @Override
    public BattleRespVo getBattle(String battleId) {
        return BattleConverter.INSTANCE.modelToVo(
                battleDomainService.getBattle(battleId));
    }

    @Override
    public BattleRespVo act(String battleId, PlayerAction action, String cardId) {
        return BattleConverter.INSTANCE.modelToVo(
                battleDomainService.act(battleId, action, cardId));
    }

    @Override
    public BattleRespVo runAiAction(String battleId) {
        return BattleConverter.INSTANCE.modelToVo(
                battleDomainService.runAiAction(battleId));
    }
}
