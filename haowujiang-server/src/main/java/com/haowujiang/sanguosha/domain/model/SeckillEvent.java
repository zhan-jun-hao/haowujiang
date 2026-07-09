package com.haowujiang.sanguosha.domain.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 秒杀事件领域模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillEvent {

    /**
     * 事件 ID
     */
    private String id;

    /**
     * 事件级别
     */
    private String level;

    /**
     * 事件消息
     */
    private String message;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
