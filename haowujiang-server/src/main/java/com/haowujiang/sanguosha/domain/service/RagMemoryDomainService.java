package com.haowujiang.sanguosha.domain.service;

import com.haowujiang.sanguosha.domain.model.BattleState;
import com.haowujiang.sanguosha.domain.model.RagMemorySnippet;
import com.haowujiang.sanguosha.infrastructure.persistence.po.RagDocument;
import java.util.List;

public interface RagMemoryDomainService {

    void indexDocument(RagDocument document);

    List<RagMemorySnippet> searchForBattle(BattleState state);
}
