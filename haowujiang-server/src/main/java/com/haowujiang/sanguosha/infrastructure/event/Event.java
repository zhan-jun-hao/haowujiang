package com.haowujiang.sanguosha.infrastructure.event;

import com.haowujiang.sanguosha.infrastructure.enums.EventSource;
import com.haowujiang.sanguosha.infrastructure.enums.EventType;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 通用事件外壳
 * <p>
 * 所有消息都套用这个外壳，payload 里放具体业务数据
 * </p>
 */
@Data
public class Event <T> {

    /**
     * 事件唯一 ID
     */
    private String eventId;

    /**
     * @see EventType
     */
    private EventType eventType;

    /**
     * 事件发生时间
     */
    private LocalDateTime eventTime;

    /**
     * 链路追踪 ID
     */
    private String traceId;

    /**
     * 事件版本号
     */
    private Integer version = 1;

    /**
     * 事件来源服务
     * @see EventSource
     */
    private EventSource source;

    /**
     * 具体业务数据（可以是任意对象）
     */
    private T payload;
}