package com.haowujiang.sanguosha.infrastructure.event;

import com.haowujiang.sanguosha.infrastructure.enums.EventSource;
import com.haowujiang.sanguosha.infrastructure.enums.EventType;
import com.haowujiang.sanguosha.infrastructure.security.context.UserContextHolder;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 事件构建器
 */
public class EventBuilder {

    public static <T> Event<T> build(EventType eventType, EventSource source, T payload) {
        Event<T> event = new Event<>();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType(eventType);
        event.setEventTime(LocalDateTime.now());
        event.setTraceId(UserContextHolder.getTraceId());
        event.setVersion(1);
        event.setSource(source);
        event.setPayload(payload);
        return event;
    }

}
