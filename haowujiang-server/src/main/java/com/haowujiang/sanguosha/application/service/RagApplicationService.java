package com.haowujiang.sanguosha.application.service;

import com.haowujiang.sanguosha.infrastructure.common.PageResult;
import com.haowujiang.sanguosha.interfaces.vo.rag.request.RagDocumentPageQueryReqVo;
import com.haowujiang.sanguosha.interfaces.vo.rag.response.RagDocumentBasicVo;

import java.util.List;

public interface RagApplicationService {

    List<RagDocumentBasicVo> listDocuments(String generalCode);

    RagDocumentBasicVo addDocument(String generalCode, String title, String content);

    PageResult<RagDocumentBasicVo> pageQueryDocuments(String generalCode, RagDocumentPageQueryReqVo query);
}
