package com.haowujiang.sanguosha.application.service;

import com.haowujiang.sanguosha.application.vo.rag.response.RagDocumentBasicVo;
import java.util.List;

public interface RagApplicationService {

    List<RagDocumentBasicVo> listDocuments(String generalCode);

    RagDocumentBasicVo addDocument(String generalCode, String title, String content);
}
