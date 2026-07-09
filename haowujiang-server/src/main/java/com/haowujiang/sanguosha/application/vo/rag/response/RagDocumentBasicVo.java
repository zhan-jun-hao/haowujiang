package com.haowujiang.sanguosha.application.vo.rag.response;

import lombok.Data;

/**
 * RAG 文档基础视图对象，对应 ragDocument 表
 */
@Data
public class RagDocumentBasicVo {

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 武将编码
     */
    private String generalCode;

    /**
     * 知识条目标题
     */
    private String title;

    /**
     * 知识条目内容
     */
    private String content;

    /**
     * 更新时间
     */
    private String updateTime;
}
