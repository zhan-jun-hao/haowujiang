package com.haowujiang.sanguosha.interfaces.vo.seckill.response;

import lombok.Data;

/**
 * 秒杀事件响应对象。
 */
@Data
public class SeckillEventRespVo {

    /**
     * 事件 ID。
     */
    private String id;

    /**
     * 事件级别。
     */
    private String level;

    /**
     * 事件消息。
     */
    private String message;

    /**
     * 创建时间。
     */
    private String createdAt;
}
