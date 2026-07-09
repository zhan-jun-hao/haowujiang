package com.haowujiang.sanguosha.domain.service;

import com.haowujiang.sanguosha.infrastructure.persistence.po.RagDocument;
import java.util.List;

public interface RagDomainService {

    List<RagDocument> listDocuments(String generalCode);

    RagDocument addDocument(String generalCode, String title, String content);
}
