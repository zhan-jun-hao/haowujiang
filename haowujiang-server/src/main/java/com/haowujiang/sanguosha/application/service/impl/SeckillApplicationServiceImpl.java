package com.haowujiang.sanguosha.application.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.haowujiang.sanguosha.application.converter.SeckillConverter;
import com.haowujiang.sanguosha.application.port.SeckillEventPublisher;
import com.haowujiang.sanguosha.application.service.SeckillApplicationService;
import com.haowujiang.sanguosha.application.vo.seckill.response.SeckillResultRespVo;
import com.haowujiang.sanguosha.application.vo.seckill.response.SeckillStateRespVo;
import com.haowujiang.sanguosha.domain.enums.GeneralCode;
import com.haowujiang.sanguosha.domain.event.SeckillOrderCreatedEvent;
import com.haowujiang.sanguosha.domain.exception.BusinessException;
import com.haowujiang.sanguosha.domain.model.SeckillEvent;
import com.haowujiang.sanguosha.domain.model.SeckillResult;
import com.haowujiang.sanguosha.domain.model.SeckillState;
import com.haowujiang.sanguosha.domain.service.GeneralDomainService;
import com.haowujiang.sanguosha.domain.service.SeckillOrderDomainService;
import com.haowujiang.sanguosha.domain.service.SeckillStockDomainService;
import com.haowujiang.sanguosha.infrastructure.persistence.po.SeckillOrder;
import com.haowujiang.sanguosha.infrastructure.persistence.po.SeckillStock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 秒杀应用服务实现类
 */
@Service
@RequiredArgsConstructor
public class SeckillApplicationServiceImpl implements SeckillApplicationService {

    private static final List<String> STEPS = List.of(
            "Sentinel 保护秒杀入口流量",
            "SeckillStockDomainService 扣减秒杀库存",
            "SeckillOrderDomainService 创建秒杀订单",
            "UserGeneralDomainService 发放武将",
            "Kafka 发布秒杀订单创建事件"
    );

    private final GeneralDomainService generalDomainService;

    private final SeckillOrderDomainService seckillOrderDomainService;

    private final SeckillStockDomainService seckillStockDomainService;

    private final SeckillEventPublisher seckillEventPublisher;

    private final StringRedisTemplate stringRedisTemplate;

    private final DefaultRedisScript<Long> seckillScript;

    private final DefaultRedisScript<Long> compensateScript;

    @Override
    public SeckillStateRespVo getState(String generalCode) {
        ensureOpenGeneral(generalCode);
        return SeckillConverter.INSTANCE.modelToVo(buildState(generalCode));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @SentinelResource(value = "seckillGeneral", blockHandler = "claimBlocked")
    public SeckillResultRespVo claim(Long userId, String generalCode) {
        // 1.验证武将是否存在
        ensureOpenGeneral(generalCode);
        // 2.调用lua脚本扣减库存
        String stockKey = "seckill:stock:" + generalCode;
        String userSetKey = "seckill:order:users:" + generalCode;
        warmUpRedisStock(generalCode, stockKey, userSetKey);
        Long result = stringRedisTemplate.execute(seckillScript,
                Arrays.asList(stockKey, userSetKey),
                String.valueOf(userId));
        if (Objects.isNull(result)) {
            throw new BusinessException("lua返回为空");
        }
        int code = result.intValue();
        if(code == 1) {
            throw new BusinessException("库存不足");
        } else if(code == 2) {
            throw new BusinessException("重复下单");
        } else if(code != 0) {
            throw new BusinessException("lua返回异常");
        }
        // 3.同步扣减 MySQL 库存 失败时补偿 Redis
        if (!seckillStockDomainService.deductStock(generalCode)) {
            compensateRedis(stockKey, userSetKey, userId);
            throw new BusinessException("库存不足");
        }
        // 4.扣减库存成功异步下单
        Long orderId = IdUtil.getSnowflakeNextId();
        try {
            seckillEventPublisher.publish(new SeckillOrderCreatedEvent(orderId, userId, generalCode));
        } catch (RuntimeException exception) {
            compensateRedis(stockKey, userSetKey, userId);
            throw exception;
        }

        return SeckillConverter.INSTANCE.modelToVo(
                buildResult(true, "抢购成功", "订单已创建，武将已发放到用户武将中心", generalCode));
    }

    public SeckillResultRespVo claimBlocked(Long userId, String generalCode, BlockException exception) {
        throw new BusinessException(HttpStatus.TOO_MANY_REQUESTS, "秒杀请求过于频繁，请稍后再试");
    }

    private void ensureOpenGeneral(String generalCode) {
        generalDomainService.getGeneralByCode(generalCode);
        if (!GeneralCode.ZHAO_YUN.getValue().equals(generalCode)) {
            throw new BusinessException("当前仅开放赵云秒杀");
        }
    }

    private void warmUpRedisStock(String generalCode, String stockKey, String userSetKey) {
        if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey(stockKey))) {
            SeckillStock stock = seckillStockDomainService.getByGeneralCode(generalCode);
            stringRedisTemplate.opsForValue().set(stockKey, String.valueOf(stock == null ? 0 : stock.getAvailableStock()));
        }
        if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey(userSetKey))) {
            List<String> userIds = seckillOrderDomainService.listByGeneralCode(generalCode)
                    .stream()
                    .map(order -> String.valueOf(order.getUserId()))
                    .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
            if (!userIds.isEmpty()) {
                stringRedisTemplate.opsForSet().add(userSetKey, userIds.toArray(String[]::new));
            }
        }
    }

    private void compensateRedis(String stockKey, String userSetKey, Long userId) {
        stringRedisTemplate.execute(compensateScript,
                Arrays.asList(stockKey, userSetKey),
                String.valueOf(userId));
    }

    private SeckillResult buildResult(boolean ok, String title, String message, String generalCode) {
        return new SeckillResult(ok, title, message, STEPS, buildState(generalCode));
    }

    private SeckillState buildState(String generalCode) {
        SeckillStock stock = seckillStockDomainService.getByGeneralCode(generalCode);
        List<SeckillOrder> orders = seckillOrderDomainService.listByGeneralCode(generalCode);
        List<Long> claimedUserIds = orders.stream()
                .map(SeckillOrder::getUserId)
                .distinct()
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        List<SeckillEvent> events = orders.stream()
                .limit(5)
                .map(order -> new SeckillEvent(
                        "event-" + order.getOrderNo(),
                        "info",
                        order.getUserId() + " 已抢到 " + order.getGeneralCode(),
                        order.getCreateTime()
                ))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

        return new SeckillState(
                generalCode,
                stock == null ? 0 : stock.getAvailableStock(),
                stock == null ? 0 : stock.getTotalStock(),
                claimedUserIds,
                orders,
                events
        );
    }
}
