package com.haowujiang.sanguosha.application.vo.ai.response;

import java.util.List;
import lombok.Data;

/**
 * 客户端 AI 聊天响应对象
 */
@Data
public class ClientAiChatRespVo {

    /**
     * 会话 ID
     */
    private String conversationId;

    /**
     * AI 回答
     */
    private String answer;

    /**
     * RAG 引用列表
     */
    private List<RagReferenceBasicVo> references;
}
