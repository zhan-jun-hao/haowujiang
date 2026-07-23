package com.haowujiang.sanguosha.interfaces.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RocketMQMessageListener(
        topic = "%DLQ%seckill-order-consumer", // 死信队列的 Topic 固定格式
        consumerGroup = "seckill-dead-letter-consumer" // 一个独立的消费者组
)
public class SeckillDeadLetterConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        log.warn("收到死信消息: {}", message);
        // 1. 打印或记录消息，以便人工排查
        // 2. 可以发送告警通知
        // 3. 可以将消息存入数据库，等待后续人工或程序处理
        // 注意：这里不要抛出异常，否则会再次进入重试死循环
    }
}
