package com.haowujiang.sanguosha.interfaces.mq;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.haowujiang.sanguosha.domain.service.UserGeneralDomainService;
import com.haowujiang.sanguosha.infrastructure.event.Event;
import com.haowujiang.sanguosha.infrastructure.event.payload.SeckillOrderCreatedEventPayload;
import com.haowujiang.sanguosha.infrastructure.event.topic.TopicConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = TopicConstants.SECKILL_TOPIC,
        consumerGroup = "seckill-order-consumer"
)
public class SecKillOrderConsumer implements RocketMQListener<String> {

    private final UserGeneralDomainService userGeneralDomainService;

    @Override
    public void onMessage(String message) {
        try {
            // 1. 解析事件
            Event<SeckillOrderCreatedEventPayload> event = JSONObject.parseObject(
                    message,
                    new TypeReference<>() {}
            );

            SeckillOrderCreatedEventPayload payload = event.getPayload();
            Long userId = payload.getUserId();
            String generalCode = payload.getGeneralCode();
            Long orderId = payload.getOrderId();

            log.info("收到秒杀订单消息: orderId={}, userId={}, generalCode={}",
                    orderId, userId, generalCode);

            // 2. 发放武将（幂等）
            userGeneralDomainService.grantGeneral(userId, generalCode);

            log.info("发放武将成功: userId={}, generalCode={}", userId, generalCode);

        } catch (DuplicateKeyException e) {
            // 重复消费：直接 ACK
            log.warn("重复消息，已处理过: {}", message);
        } catch (Exception e) {
            log.error("消费失败", e);
            throw new RuntimeException(e);
        }
    }
}