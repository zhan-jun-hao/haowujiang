package com.haowujiang.sanguosha.application.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haowujiang.sanguosha.application.converter.SeckillConverter;
import com.haowujiang.sanguosha.application.service.SeckillActivityApplicationService;
import com.haowujiang.sanguosha.domain.exception.BusinessException;
import com.haowujiang.sanguosha.domain.service.GeneralDomainService;
import com.haowujiang.sanguosha.domain.service.SeckillActivityDomainService;
import com.haowujiang.sanguosha.infrastructure.cache.CacheObjectValue;
import com.haowujiang.sanguosha.infrastructure.common.PageResult;
import com.haowujiang.sanguosha.infrastructure.contants.RedisKeyConstants;
import com.haowujiang.sanguosha.infrastructure.persistence.po.General;
import com.haowujiang.sanguosha.infrastructure.persistence.po.SeckillActivity;
import com.haowujiang.sanguosha.interfaces.vo.seckill.request.CreateSeckillActivityReqVo;
import com.haowujiang.sanguosha.interfaces.vo.seckill.request.SeckillActivityPageQueryReqVo;
import com.haowujiang.sanguosha.interfaces.vo.seckill.response.SeckillActivityRespVo;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

/**
 * 秒杀活动应用服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeckillActivityApplicationServiceImpl implements SeckillActivityApplicationService {

    private final SeckillActivityDomainService seckillActivityDomainService;
    private final GeneralDomainService generalDomainService;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public PageResult<SeckillActivityRespVo> pageQuery(SeckillActivityPageQueryReqVo query) {
        IPage<SeckillActivity> page = seckillActivityDomainService.pageQuery(query);
        List<SeckillActivityRespVo> records = page.getRecords().stream()
                .map(SeckillConverter.INSTANCE::activityToVo)
                .collect(Collectors.toList());
        return PageResult.success(page, records);
    }

    @Override
    public SeckillActivityRespVo getByActivityCode(String activityCode) {
        String dataKey = RedisKeyConstants.SECKILL_GENERAL_KEY_PREFIX + activityCode;
        String lockKey = RedisKeyConstants.SECKILL_LOCK_KEY_PREFIX + activityCode;
        RBucket<String> bucket = redissonClient.getBucket(dataKey);
        String objJson = bucket.get();
        long currentStamp = System.currentTimeMillis();
        // 物理过期时间：2 小时 + 随机 0-30 分钟
        long baseTtl = 2 * 60 * 60 * 1000;  // 2 小时
        long randomOffset = ThreadLocalRandom.current().nextInt(0, 30 * 60 * 1000);  // 0-30 分钟
        long ttlMillis = baseTtl + randomOffset;

        // 逻辑过期时间：1 小时 + 随机 0-15 分钟
        long logicBase = 60 * 60 * 1000;  // 1 小时
        long logicOffset = ThreadLocalRandom.current().nextInt(0, 15 * 60 * 1000);  // 0-15 分钟
        long logicExpire = System.currentTimeMillis() + logicBase + logicOffset;
        long logicTtl = logicExpire + currentStamp;

        // 1. 缓存存在
        if (StrUtil.isNotBlank(objJson)) {
            // 空值缓存 防止缓存穿透
            if ("NULL".equals(objJson)) {
                log.info("数据库里根本没这个数据!");
                return null;
            }

            CacheObjectValue<SeckillActivityRespVo> cacheObjectValue
                    = JSONObject.parseObject(objJson, new TypeReference<>() {});

            // 2. 逻辑未过期 → 直接返回
            if (cacheObjectValue.getExpire() > currentStamp) {
                return cacheObjectValue.getData();
            }

            // 3. 逻辑已过期 → 先返回旧数据（用户 0 等待）
            // 尝试获取锁，只让一个线程去刷新
            Boolean locked = stringRedisTemplate.opsForValue()
                    .setIfAbsent(lockKey, "1", Duration.ofSeconds(5));

            if (Boolean.TRUE.equals(locked)) {
                // 拿到锁 → 异步刷新（用户不等待）
                CompletableFuture.runAsync(() -> {
                    try {
                        // 双重检查：其他线程可能已刷新
                        String cacheAgain = bucket.get();
                        if (StrUtil.isNotBlank(cacheAgain) && !"NULL".equals(cacheAgain)) {
                            CacheObjectValue<SeckillActivityRespVo> existing =
                                    JSONObject.parseObject(cacheAgain, new TypeReference<>() {});
                            if (existing.getExpire() > currentStamp) {
                                return; // 已被其他线程刷新
                            }
                        }
                        // 查 DB
                        SeckillActivity seckillActivity = seckillActivityDomainService.getByActivityCode(activityCode);
                        if (seckillActivity == null) {
                            bucket.set("NULL", Duration.ofMillis(ttlMillis));
                            return;
                        }
                        // 写缓存
                        SeckillActivityRespVo vo = SeckillConverter.INSTANCE.activityToVo(seckillActivity);
                        CacheObjectValue<SeckillActivityRespVo> cache =
                                new CacheObjectValue<>(vo, logicTtl);
                        bucket.set(JSONObject.toJSONString(cache), Duration.ofMillis(ttlMillis));
                        log.info("异步刷新缓存成功: activityCode={}", activityCode);

                    } catch (Exception e) {
                        log.error("异步刷新缓存失败: activityCode={}", activityCode, e);
                    } finally {
                        stringRedisTemplate.delete(lockKey);
                    }
                });
            } else {
                log.debug("缓存正在刷新中，返回旧数据: activityCode={}", activityCode);
            }

            // 返回旧数据（用户 0 等待）
            return cacheObjectValue.getData();
        }

        // 4. 缓存不存在 → 同步加载（物理过期）
        return loadFromDBAndCache(activityCode, ttlMillis, logicTtl);
    }

    private SeckillActivityRespVo loadFromDBAndCache(String activityCode, long ttl, long logicTtl) {
        String dataKey = RedisKeyConstants.SECKILL_GENERAL_KEY_PREFIX + activityCode;
        String lockKey = RedisKeyConstants.SECKILL_LOCK_KEY_PREFIX + activityCode;
        RBucket<String> bucket = redissonClient.getBucket(dataKey);

        int retryCount = 0;
        int maxRetries = 30;

        while (retryCount < maxRetries) {
            // 轻量级互斥锁（1 次 Redis 命令）
            Boolean locked = stringRedisTemplate.opsForValue()
                    .setIfAbsent(lockKey, "1", Duration.ofSeconds(3));

            if (Boolean.TRUE.equals(locked)) {
                try {
                    // 双重检查
                    String cacheAgain = bucket.get();
                    if (StrUtil.isNotBlank(cacheAgain)) {
                        if ("NULL".equals(cacheAgain)) {
                            return null;
                        }
                        CacheObjectValue<SeckillActivityRespVo> wrapper =
                                JSONObject.parseObject(cacheAgain, new TypeReference<>(){});
                        return wrapper.getData();
                    }

                    // 查 DB + 写缓存
                    SeckillActivity seckillActivity = seckillActivityDomainService.getByActivityCode(activityCode);
                    if (seckillActivity == null) {
                        bucket.set("NULL", Duration.ofMinutes(5));
                        return null;
                    }

                    SeckillActivityRespVo vo = SeckillConverter.INSTANCE.activityToVo(seckillActivity);
                    CacheObjectValue<SeckillActivityRespVo> cache =
                            new CacheObjectValue<>(vo, logicTtl);
                    bucket.set(JSONObject.toJSONString(cache), Duration.ofMillis(ttl));
                    return vo;

                } finally {
                    stringRedisTemplate.delete(lockKey);
                }
            }

            retryCount++;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        log.warn("获取缓存超时: activityCode={}", activityCode);
        return null;
    }

    @Override
    public SeckillActivityRespVo getByActivityCodeDirect(String activityCode) {
        SeckillActivity seckillActivity = seckillActivityDomainService.getByActivityCode(activityCode);
        if (seckillActivity == null) {
            return null;
        }
        return SeckillConverter.INSTANCE.activityToVo(seckillActivity);
    }

    @Override
    public SeckillActivityRespVo createActivity(CreateSeckillActivityReqVo reqVo) {
        // 1. 验证武将是否存在
        General general = generalDomainService.getGeneralByCode(reqVo.getGeneralCode());

        // 2. 验证时间
        if (!reqVo.getEndTime().isAfter(reqVo.getStartTime())) {
            throw new BusinessException("结束时间必须在开始时间之后");
        }

        // 3. 构建活动
        SeckillActivity activity = getSeckillActivity(reqVo, general);

        seckillActivityDomainService.createActivity(activity);
        return SeckillConverter.INSTANCE.activityToVo(activity);
    }

    private static SeckillActivity getSeckillActivity(CreateSeckillActivityReqVo reqVo, General general) {
        SeckillActivity activity = new SeckillActivity();
        activity.setActivityCode("SK" + IdUtil.getSnowflakeNextId());
        activity.setActivityName(reqVo.getActivityName());
        activity.setGeneralCode(reqVo.getGeneralCode());
        activity.setGeneralName(general.getName());
        activity.setStock(reqVo.getStock());
        activity.setAvailableStock(reqVo.getStock());
        activity.setLimitPerUser(reqVo.getLimitPerUser());
        activity.setStartTime(reqVo.getStartTime());
        activity.setEndTime(reqVo.getEndTime());
        activity.setStatus(0); // 待开始
        return activity;
    }

    @Override
    public void offlineActivity(String activityCode) {
        SeckillActivity activity = seckillActivityDomainService.getByActivityCode(activityCode);
        if (activity.getStatus() == 2) {
            throw new BusinessException("已结束的活动无法下架");
        }
        if (activity.getStatus() == 3) {
            throw new BusinessException("活动已下架，无需重复操作");
        }
        seckillActivityDomainService.updateStatus(activity.getId(), 3);
    }

    @Override
    public void enableActivity(String activityCode) {
        SeckillActivity activity = seckillActivityDomainService.getByActivityCode(activityCode);
        if (activity.getStatus() == 1) {
            throw new BusinessException("活动已开启，无需重复操作");
        }
        if (activity.getStatus() == 2) {
            throw new BusinessException("已结束的活动无法开启");
        }
        if (activity.getStatus() == 3) {
            throw new BusinessException("已下架的活动无法开启，请重新上架");
        }
        seckillActivityDomainService.updateStatus(activity.getId(), 1);
    }
}
