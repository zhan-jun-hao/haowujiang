package com.haowujiang.sanguosha.infrastructure.messaging;

import com.haowujiang.sanguosha.application.port.SeckillEventPublisher;
import com.haowujiang.sanguosha.domain.event.SeckillOrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class KafkaSeckillEventPublisher implements SeckillEventPublisher {

    public static final String TOPIC = "general-seckill-order-created";

    private final KafkaTemplate<String, SeckillOrderCreatedEvent> kafkaTemplate;

    @Override
    public void publish(SeckillOrderCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event.getUserId() + ":" + event.getGeneralCode(), event);
    }
}
