package com.haowujiang.sanguosha.application.vo.ai.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 客户端 AI 聊天请求对象
 */
@Data
public class ClientAiChatReqVo {

    /**
     * 用户问题
     */
    @NotBlank
    private String question;

    /**
     * 会话 ID，不传则后端生成
     */
    private String conversationId;

    /**
     * 指定检索的武将编码，不传则全局检索
     */
    private String generalCode;

    /**
     * 检索数量
     */
    private Integer topK;
}
