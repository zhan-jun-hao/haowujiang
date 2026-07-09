package com.haowujiang.sanguosha.infrastructure.messaging;

import com.haowujiang.sanguosha.application.port.SeckillEventPublisher;
import com.haowujiang.sanguosha.domain.event.SeckillOrderCreatedEvent;
import com.haowujiang.sanguosha.domain.service.SeckillOrderDomainService;
import com.haowujiang.sanguosha.domain.service.UserGeneralDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Kafka 未开启时使用的本地下单发布器
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "false", matchIfMissing = true)
public class LocalSeckillEventPublisher implements SeckillEventPublisher {

    private final SeckillOrderDomainService seckillOrderDomainService;

    private final UserGeneralDomainService userGeneralDomainService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(SeckillOrderCreatedEvent event) {
        // 1. 创建秒杀订单
        seckillOrderDomainService.createOrder(event.getOrderId(), event.getUserId(), event.getGeneralCode());

        // 2. 发放武将
        userGeneralDomainService.grantGeneral(event.getUserId(), event.getGeneralCode());
    }
}
