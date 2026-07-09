package com.haowujiang.sanguosha.domain.service;

import com.haowujiang.sanguosha.domain.model.BattleAiDecision;
import com.haowujiang.sanguosha.domain.model.BattleState;

public interface AiDecisionDomainService {

    BattleAiDecision decide(BattleState state);
}
