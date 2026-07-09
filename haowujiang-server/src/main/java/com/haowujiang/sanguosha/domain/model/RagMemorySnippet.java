package com.haowujiang.sanguosha.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RAG 记忆片段领域模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RagMemorySnippet {

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
     * 知识内容
     */
    private String content;

    /**
     * 相似度分数
     */
    private Double score;
}
