package com.haowujiang.sanguosha.infrastructure.mq;

import com.alibaba.fastjson.JSONObject;
import com.haowujiang.sanguosha.infrastructure.event.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class RocketMQAsyncService {

    private final RocketMQTemplate rocketMQTemplate;

    /**
     * 异步发送消息（带回调）
     *
     * @param topic    主题
     * @param event    事件对象
     * @param onSuccess 成功回调（处理发送成功后的逻辑） lambda表达式
     * @param onFailure 失败回调（处理发送失败后的逻辑） lambda表达式
     */
    public void publishAsync(String topic, Event<?> event,
                             Consumer<SendResult> onSuccess,
                             Consumer<Throwable> onFailure) {
        try {
            String json = JSONObject.toJSONString(event);
            log.info("发布事件: eventId={}, type={}",
                    event.getEventId(), event.getEventType());

            rocketMQTemplate.asyncSend(topic,
                    MessageBuilder.withPayload(json).build(),
                    new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {
                            log.info("事件发布成功: eventId={}, msgId={}",
                                    event.getEventId(), sendResult.getMsgId());
                            if (onSuccess != null) {
                                // onSuccess.accept(sendResult) 是 函数式接口的调用方式 ——它执行的是调用方传入的 Lambda 表达式
                                onSuccess.accept(sendResult);
                            }
                        }

                        @Override
                        public void onException(Throwable e) {
                            log.error("事件发布失败: eventId={}", event.getEventId(), e);
                            if (onFailure != null) {
                                onFailure.accept(e);
                            }
                        }
                    });

        } catch (Exception e) {
            log.error("事件发送异常: eventId={}", event.getEventId(), e);
            if (onFailure != null) {
                onFailure.accept(e);
            }
        }
    }

    /**
     * 异步发送消息（不关心结果）
     */
    public void publishAsync(String topic, Event<?> event) {
        publishAsync(topic, event, null, null);
    }
}