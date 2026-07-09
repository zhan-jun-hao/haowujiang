package com.haowujiang.sanguosha.application.port;

import com.haowujiang.sanguosha.domain.event.SeckillOrderCreatedEvent;

public interface SeckillEventPublisher {

    void publish(SeckillOrderCreatedEvent event);
}
