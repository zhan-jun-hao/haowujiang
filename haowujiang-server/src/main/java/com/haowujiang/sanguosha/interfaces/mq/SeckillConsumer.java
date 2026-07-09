package com.haowujiang.sanguosha.interfaces.mq;

import com.haowujiang.sanguosha.domain.event.SeckillOrderCreatedEvent;
import com.haowujiang.sanguosha.domain.service.SeckillOrderDomainService;
import com.haowujiang.sanguosha.domain.service.UserGeneralDomainService;
import com.haowujiang.sanguosha.infrastructure.messaging.KafkaSeckillEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public class SeckillConsumer {

    private final SeckillOrderDomainService seckillOrderDomainService;
    private final UserGeneralDomainService userGeneralDomainService;

    @KafkaListener(topics = KafkaSeckillEventPublisher.TOPIC)
    @Transactional(rollbackFor = Exception.class)
    public void seckill(ConsumerRecord<String, SeckillOrderCreatedEvent> record,
                        Acknowledgment ack) {

        SeckillOrderCreatedEvent event = record.value();

        try {
            Long userId = event.getUserId();
            String generalCode = event.getGeneralCode();
            Long orderId = event.getOrderId();
            // 1. 创建秒杀订单
            seckillOrderDomainService.createOrder(orderId, userId, generalCode);

            // 2. 发放武将
            userGeneralDomainService.grantGeneral(userId, generalCode);

            // 3. 成功后 ack
            ack.acknowledge();

            log.info("秒杀订单消费成功, orderId={} , userId={}, generalCode={}", orderId, userId, generalCode);

        } catch (DuplicateKeyException e) {
            log.info("秒杀重复消息，orderId={}", event.getOrderId());
            ack.acknowledge();

        } catch (Exception e) {
            log.error("秒杀消息消费失败，orderId={}", event.getOrderId(), e);
            throw e;
        }
    }
}
