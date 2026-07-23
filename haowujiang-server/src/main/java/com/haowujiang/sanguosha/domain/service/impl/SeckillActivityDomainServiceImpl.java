package com.haowujiang.sanguosha.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haowujiang.sanguosha.domain.exception.BusinessException;
import com.haowujiang.sanguosha.domain.service.SeckillActivityDomainService;
import com.haowujiang.sanguosha.infrastructure.persistence.mapper.SeckillActivityMapper;
import com.haowujiang.sanguosha.infrastructure.persistence.po.SeckillActivity;
import com.haowujiang.sanguosha.interfaces.vo.seckill.request.SeckillActivityPageQueryReqVo;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 秒杀活动领域服务实现
 */
@Service
@RequiredArgsConstructor
public class SeckillActivityDomainServiceImpl implements SeckillActivityDomainService {

    private final SeckillActivityMapper seckillActivityMapper;

    @Override
    public IPage<SeckillActivity> pageQuery(SeckillActivityPageQueryReqVo query) {
        LambdaQueryWrapper<SeckillActivity> wrapper = Wrappers.lambdaQuery(SeckillActivity.class)
                .eq(Objects.nonNull(query.getStatus()), SeckillActivity::getStatus, query.getStatus())
                .eq(StrUtil.isNotBlank(query.getGeneralCode()), SeckillActivity::getGeneralCode, query.getGeneralCode())
                .orderByDesc(SeckillActivity::getCreatedTime);
        return seckillActivityMapper.selectPage(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public SeckillActivity getByActivityCode(String activityCode) {
        LambdaQueryWrapper<SeckillActivity> wrapper = Wrappers.lambdaQuery(SeckillActivity.class)
                .eq(SeckillActivity::getActivityCode, activityCode);
        SeckillActivity activity = seckillActivityMapper.selectOne(wrapper);
        if (Objects.isNull(activity)) {
            throw new BusinessException("秒杀活动不存在");
        }
        return activity;
    }

    @Override
    public SeckillActivity getActiveByGeneralCode(String generalCode) {
        LambdaQueryWrapper<SeckillActivity> wrapper = Wrappers.lambdaQuery(SeckillActivity.class)
                .eq(SeckillActivity::getGeneralCode, generalCode)
                .in(SeckillActivity::getStatus, List.of(0, 1))
                .orderByDesc(SeckillActivity::getCreatedTime)
                .last("LIMIT 1");
        return seckillActivityMapper.selectOne(wrapper);
    }

    @Override
    public SeckillActivity createActivity(SeckillActivity activity) {
        // 检查同一武将是否有进行中的活动
        SeckillActivity existing = getActiveByGeneralCode(activity.getGeneralCode());
        if (existing != null) {
            throw new BusinessException("该武将已有进行中的秒杀活动，活动编码：" + existing.getActivityCode());
        }
        seckillActivityMapper.insert(activity);
        return activity;
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        SeckillActivity activity = new SeckillActivity();
        activity.setId(id);
        activity.setStatus(status);
        seckillActivityMapper.updateById(activity);
    }

    @Override
    public List<SeckillActivity> listActive() {
        LambdaQueryWrapper<SeckillActivity> wrapper = Wrappers.lambdaQuery(SeckillActivity.class)
                .in(SeckillActivity::getStatus, List.of(0, 1))
                .orderByDesc(SeckillActivity::getCreatedTime);
        return seckillActivityMapper.selectList(wrapper);
    }

    @Override
    public boolean deductStock(String generalCode) {
        return seckillActivityMapper.update(Wrappers.lambdaUpdate(SeckillActivity.class)
                .setSql("availableStock = availableStock - 1")
                .eq(SeckillActivity::getGeneralCode, generalCode)
                .in(SeckillActivity::getStatus, List.of(0, 1))
                .gt(SeckillActivity::getAvailableStock, 0)) > 0;
    }
}
