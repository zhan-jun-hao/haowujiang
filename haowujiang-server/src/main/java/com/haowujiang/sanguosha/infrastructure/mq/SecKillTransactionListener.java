package com.haowujiang.sanguosha.infrastructure.mq;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.haowujiang.sanguosha.domain.service.SeckillOrderDomainService;
import com.haowujiang.sanguosha.infrastructure.event.Event;
import com.haowujiang.sanguosha.infrastructure.event.payload.SeckillOrderCreatedEventPayload;
import com.haowujiang.sanguosha.infrastructure.persistence.po.SeckillOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 秒杀事务监听器
 * 1. executeLocalTransaction：执行本地事务（创建订单）
 * 2. checkLocalTransaction：事务回查（Broker 询问订单是否存在）
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQTransactionListener
public class SecKillTransactionListener implements RocketMQLocalTransactionListener {

    private final SeckillOrderDomainService seckillOrderDomainService;

    /**
     * 发送半消息后执行本地事务
     * 返回值决定半消息是 COMMIT 还是 ROLLBACK
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        try {
            // 1. 解析消息
            String json = new String((byte[]) msg.getPayload());
            Event<SeckillOrderCreatedEventPayload> event = JSONObject.parseObject(json, new TypeReference<>(){});

            SeckillOrderCreatedEventPayload payload = event.getPayload();
            Long orderId = payload.getOrderId();
            Long userId = payload.getUserId();
            String generalCode = payload.getGeneralCode();

            log.info("执行本地事务: orderId={}, userId={}, generalCode={}",
                    orderId, userId, generalCode);

            // 2. 创建秒杀订单（本地事务）
            seckillOrderDomainService.createOrder(orderId, userId, generalCode);

            log.info("本地事务执行成功: orderId={}", orderId);

            // 3. 本地事务成功 → COMMIT 消息
            // 消费者将收到消息，开始处理后续业务（发放武将等）
            return RocketMQLocalTransactionState.COMMIT;

        } catch (Exception e) {
            log.error("本地事务执行失败", e);
            // 本地事务失败 → ROLLBACK 消息（消息被丢弃）
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    /**
     * 事务回查（Broker 未收到 COMMIT/ROLLBACK 时调用）
     * 防止网络抖动、Producer 宕机导致消息状态不确定
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        try {
            // 1. 解析消息
            String json = new String((byte[]) msg.getPayload());
            Event<SeckillOrderCreatedEventPayload> event = JSONObject.parseObject(json, new TypeReference<>(){});

            SeckillOrderCreatedEventPayload payload = event.getPayload();
            Long orderId = payload.getOrderId();

            log.info("事务回查: orderId={}", orderId);

            // 2. 查询数据库：订单是否存在？ 是否已经下单了？
            SeckillOrder seckillOrder = seckillOrderDomainService.getById(orderId);

            if (Objects.nonNull(seckillOrder)) {
                // 订单存在 → COMMIT
                log.info("事务回查: 订单存在, orderId={}", orderId);
                return RocketMQLocalTransactionState.COMMIT;
            } else {
                // 订单不存在 → ROLLBACK
                log.warn("事务回查: 订单不存在, orderId={}", orderId);
                return RocketMQLocalTransactionState.ROLLBACK;
            }

        } catch (Exception e) {
            log.error("事务回查失败", e);
            // 回查失败 → UNKNOWN，Broker 会继续回查
            return RocketMQLocalTransactionState.UNKNOWN;
        }
    }
}