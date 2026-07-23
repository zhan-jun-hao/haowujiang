package com.haowujiang.sanguosha.interfaces.vo.ai.response;

import lombok.Data;

/**
 * RAG 引用基础视图对象
 */
@Data
public class RagReferenceBasicVo {

    /**
     * 文档 ID
     */
    private Long documentId;

    /**
     * 武将编码
     */
    private String generalCode;

    /**
     * 知识标题
     */
    private String title;

    /**
     * 引用内容
     */
    private String content;

    /**
     * 相似度分数
     */
    private Double score;
}
