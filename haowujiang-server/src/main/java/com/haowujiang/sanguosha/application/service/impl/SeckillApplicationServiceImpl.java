package com.haowujiang.sanguosha.application.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.haowujiang.sanguosha.application.converter.SeckillConverter;
import com.haowujiang.sanguosha.application.service.SeckillApplicationService;
import com.haowujiang.sanguosha.infrastructure.contants.RedisKeyConstants;
import com.haowujiang.sanguosha.infrastructure.enums.EventSource;
import com.haowujiang.sanguosha.infrastructure.enums.EventType;
import com.haowujiang.sanguosha.infrastructure.event.Event;
import com.haowujiang.sanguosha.infrastructure.event.EventBuilder;
import com.haowujiang.sanguosha.infrastructure.event.payload.SeckillOrderCreatedEventPayload;
import com.haowujiang.sanguosha.infrastructure.event.topic.TopicConstants;
import com.haowujiang.sanguosha.infrastructure.mq.RocketMQAsyncService;
import com.haowujiang.sanguosha.interfaces.vo.seckill.response.SeckillActivityRespVo;
import com.haowujiang.sanguosha.interfaces.vo.seckill.response.SeckillResultRespVo;
import com.haowujiang.sanguosha.interfaces.vo.seckill.response.SeckillStateRespVo;
import com.haowujiang.sanguosha.domain.exception.BusinessException;
import com.haowujiang.sanguosha.domain.service.GeneralDomainService;
import com.haowujiang.sanguosha.domain.service.SeckillActivityDomainService;
import com.haowujiang.sanguosha.domain.service.SeckillOrderDomainService;
import com.haowujiang.sanguosha.infrastructure.persistence.po.SeckillActivity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * 秒杀应用服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeckillApplicationServiceImpl implements SeckillApplicationService {

    private final RocketMQAsyncService rocketMQAsyncService;
    private final RocketMQTemplate rocketMQTemplate;

    /**
     * 秒杀处理步骤（仅用于客户端展示，非实际执行流程）
     */
    private static final List<String> STEPS = List.of(
            "Sentinel 保护秒杀入口流量",
            "Redis Lua 脚本原子扣减秒杀库存",
            "SeckillOrderDomainService 创建秒杀订单",
            "UserGeneralDomainService 发放武将",
            "RocketMQ 事务消息发布秒杀订单创建事件"
    );

    private final GeneralDomainService generalDomainService;

    private final SeckillOrderDomainService seckillOrderDomainService;

    private final SeckillActivityDomainService seckillActivityDomainService;

    private final StringRedisTemplate stringRedisTemplate;

    private final DefaultRedisScript<Long> seckillScript;

    private final DefaultRedisScript<Long> compensateScript;

    /**
     * 查询武将秒杀状态（直接构建 VO，不经过 domain model 中转）
     */
    @Override
    public SeckillStateRespVo getState(String generalCode) {
        ensureOpenGeneral(generalCode);
        return buildState(generalCode);
    }

    /**
     * 秒杀主方法（事务消息版本）
     */
    public SeckillResultRespVo claim(Long userId, String generalCode) {
        // 1. 验证武将
        ensureOpenGeneral(generalCode);
        // 2. Redis 扣库存（原子操作，立即生效）
        String stockKey = RedisKeyConstants.STOCK_KEY_PREFIX + generalCode;
        String userSetKey = RedisKeyConstants.USER_SET_KEY_PREFIX + generalCode;
        warmUpRedisStock(generalCode, stockKey, userSetKey);

        Long result = stringRedisTemplate.execute(seckillScript,
                Arrays.asList(stockKey, userSetKey),
                String.valueOf(userId));
        if (result == null) {
            throw new BusinessException("lua返回为空");
        }
        int code = result.intValue();
        if (code == 1) {
            throw new BusinessException("库存不足");
        } else if (code == 2) {
            throw new BusinessException("重复下单");
        } else if (code != 0) {
            throw new BusinessException("lua返回异常");
        }
        // 3. 生成订单 ID
        Long orderId = IdUtil.getSnowflakeNextId();
        // 4. 构建事件
        SeckillOrderCreatedEventPayload payload = SeckillOrderCreatedEventPayload.builder()
                .userId(userId)
                .orderId(orderId)
                .generalCode(generalCode)
                .build();

        Event<SeckillOrderCreatedEventPayload> event = EventBuilder.build(
                EventType.ORDER_CREATED,
                EventSource.SECKILL_SERVICE,
                payload
        );
        String json = JSONObject.toJSONString(event);
        // 5. 发送事务消息
        //    半消息 → executeLocalTransaction 执行本地事务 → COMMIT/ROLLBACK
        try {
            rocketMQTemplate.sendMessageInTransaction(
                    TopicConstants.SECKILL_TOPIC,    // Topic
                    MessageBuilder.withPayload(json).build(),  // Message
                    null
            );
        } catch (Exception e) {
            // 发送失败，补偿 Redis
            compensateRedis(stockKey, userSetKey, userId);
            log.error("发送事务消息失败", e);
            throw new BusinessException("秒杀失败，请重试");
        }

        log.info("秒杀成功: orderId={}, userId={}, generalCode={}",
                orderId, userId, generalCode);

        return buildResult(true, "抢购成功", "订单已创建", generalCode);
    }

    @Override
    public List<SeckillActivityRespVo> listActiveActivities() {
        return seckillActivityDomainService.listActive().stream()
                .map(SeckillConverter.INSTANCE::activityToVo)
                .collect(java.util.stream.Collectors.toList());
    }

    private void ensureOpenGeneral(String generalCode) {
        // 1.武将是否存在
        generalDomainService.getGeneralByCode(generalCode);
        // 2.检查秒杀活动表中是否有该武将的活动
        SeckillActivity activity = seckillActivityDomainService.getActiveByGeneralCode(generalCode);
        if (activity == null) {
            throw new BusinessException("当前该武将没有秒杀活动");
        }
        // 3.检查秒杀时间窗口
        if (!isWithinActivityTime(activity)) {
            throw new BusinessException("当前不在秒杀活动时间内");
        }
    }

    /**
     * 判断当前时间是否在活动时间内
     */
    private boolean isWithinActivityTime(SeckillActivity activity) {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(activity.getStartTime()) && now.isBefore(activity.getEndTime());
    }

    /**
     * 从数据库预热redis键
     * @param generalCode
     * @param stockKey
     * @param userSetKey
     */
    private void warmUpRedisStock(String generalCode, String stockKey, String userSetKey) {
        if (!stringRedisTemplate.hasKey(stockKey)) {
            SeckillActivity activity = seckillActivityDomainService.getActiveByGeneralCode(generalCode);
            stringRedisTemplate.opsForValue().set(stockKey,
                    String.valueOf(activity == null ? 0 : activity.getAvailableStock()));
        }
        if (!stringRedisTemplate.hasKey(userSetKey)) {
            List<String> userIds = seckillOrderDomainService.listByGeneralCode(generalCode)
                    .stream()
                    .map(order -> String.valueOf(order.getUserId()))
                    .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
            if (!userIds.isEmpty()) {
                stringRedisTemplate.opsForSet().add(userSetKey, userIds.toArray(String[]::new));
            }
        }
    }

    /**
     * 运行lua脚本补偿机制
     * @param stockKey
     * @param userSetKey
     * @param userId
     */
    private void compensateRedis(String stockKey, String userSetKey, Long userId) {
        stringRedisTemplate.execute(compensateScript,
                Arrays.asList(stockKey, userSetKey),
                String.valueOf(userId));
    }

    /**
     * 构建秒杀结果 VO（直接构建，不经过 domain model）
     */
    private SeckillResultRespVo buildResult(boolean ok, String title, String message, String generalCode) {
        SeckillResultRespVo vo = new SeckillResultRespVo();
        vo.setOk(ok);
        vo.setTitle(title);
        vo.setMessage(message);
        vo.setSteps(STEPS);
        vo.setState(buildState(generalCode));
        return vo;
    }

    /**
     * 构建秒杀状态 VO（查询活动信息，直接组装视图）
     */
    private SeckillStateRespVo buildState(String generalCode) {
        SeckillActivity activity = seckillActivityDomainService.getActiveByGeneralCode(generalCode);

        SeckillStateRespVo vo = new SeckillStateRespVo();
        vo.setGeneralCode(generalCode);

        // 库存信息
        if (activity == null) {
            vo.setStock(0);
            vo.setTotal(0);
            return vo;
        }

        vo.setStock(activity.getAvailableStock());
        vo.setTotal(activity.getStock());

        // 活动基本信息
        vo.setActivityCode(activity.getActivityCode());
        vo.setActivityName(activity.getActivityName());
        vo.setGeneralName(activity.getGeneralName());
        vo.setStatus(activity.getStatus());
        vo.setLimitPerUser(activity.getLimitPerUser());
        vo.setStartTime(activity.getStartTime());
        vo.setEndTime(activity.getEndTime());

        return vo;
    }
}
