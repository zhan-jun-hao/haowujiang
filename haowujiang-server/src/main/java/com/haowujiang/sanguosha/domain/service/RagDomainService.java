package com.haowujiang.sanguosha.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haowujiang.sanguosha.infrastructure.persistence.po.RagDocument;
import com.haowujiang.sanguosha.interfaces.vo.rag.request.RagDocumentPageQueryReqVo;
import java.util.List;

public interface RagDomainService {

    List<RagDocument> listDocuments(String generalCode);

    RagDocument addDocument(String generalCode, String title, String content);

    IPage<RagDocument> pageQuery(String generalCode, RagDocumentPageQueryReqVo query);
}
