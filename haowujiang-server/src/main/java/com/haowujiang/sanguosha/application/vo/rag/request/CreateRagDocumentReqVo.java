package com.haowujiang.sanguosha.application.vo.rag.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建 RAG 知识库文档请求对象
 */
@Data
public class CreateRagDocumentReqVo {

    /**
     * 知识条目标题
     */
    @NotBlank
    private String title;

    /**
     * 知识条目内容
     */
    @NotBlank
    private String content;
}
