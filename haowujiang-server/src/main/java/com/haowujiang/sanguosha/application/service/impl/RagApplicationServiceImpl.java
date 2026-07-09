package com.haowujiang.sanguosha.application.service.impl;

import com.haowujiang.sanguosha.application.converter.RagDocumentConverter;
import com.haowujiang.sanguosha.application.service.RagApplicationService;
import com.haowujiang.sanguosha.application.vo.rag.response.RagDocumentBasicVo;
import com.haowujiang.sanguosha.domain.service.GeneralDomainService;
import com.haowujiang.sanguosha.domain.service.RagDomainService;
import com.haowujiang.sanguosha.domain.service.RagMemoryDomainService;
import java.util.List;

import com.haowujiang.sanguosha.infrastructure.persistence.po.General;
import com.haowujiang.sanguosha.infrastructure.persistence.po.RagDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RagApplicationServiceImpl implements RagApplicationService {

    private final RagDomainService ragDomainService;
    private final GeneralDomainService generalDomainService;
    private final RagMemoryDomainService ragMemoryDomainService;


    @Override
    public List<RagDocumentBasicVo> listDocuments(String generalCode) {
        generalDomainService.getGeneralByCode(generalCode);
        return RagDocumentConverter.INSTANCE.poToVo(ragDomainService.listDocuments(generalCode));
    }

    @Override
    public RagDocumentBasicVo addDocument(String generalCode, String title, String content) {
        generalDomainService.getGeneralByCode(generalCode);
        RagDocument document = ragDomainService.addDocument(generalCode, title, content);
        ragMemoryDomainService.indexDocument(document);
        return RagDocumentConverter.INSTANCE.poToVo(document);
    }
}
