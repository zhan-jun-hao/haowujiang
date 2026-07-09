package com.haowujiang.sanguosha.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.haowujiang.sanguosha.domain.enums.SeckillOrderStatus;
import com.haowujiang.sanguosha.domain.service.SeckillOrderDomainService;
import com.haowujiang.sanguosha.infrastructure.persistence.mapper.SeckillOrderMapper;
import com.haowujiang.sanguosha.infrastructure.persistence.po.SeckillOrder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 秒杀订单领域服务实现类
 */
@Service
@RequiredArgsConstructor
public class SeckillOrderDomainServiceImpl implements SeckillOrderDomainService {

    private final SeckillOrderMapper seckillOrderMapper;

    @Override
    public List<SeckillOrder> listByGeneralCode(String generalCode) {
        LambdaQueryWrapper<SeckillOrder> wrapper = Wrappers.lambdaQuery(SeckillOrder.class)
                .eq(SeckillOrder::getDeleted, 0)
                .eq(SeckillOrder::getGeneralCode, generalCode)
                .orderByDesc(SeckillOrder::getCreateTime);
        return seckillOrderMapper.selectList(wrapper);
    }

    @Override
    public boolean existsByUserIdAndGeneralCode(Long userId, String generalCode) {
        LambdaQueryWrapper<SeckillOrder> wrapper = Wrappers.lambdaQuery(SeckillOrder.class)
                .eq(SeckillOrder::getDeleted, 0)
                .eq(SeckillOrder::getUserId, userId)
                .eq(SeckillOrder::getGeneralCode, generalCode);
        return seckillOrderMapper.selectCount(wrapper) > 0;
    }

    @Override
    public SeckillOrder createOrder(Long orderId, Long userId, String generalCode) {
        SeckillOrder order = new SeckillOrder();
        order.setId(orderId);
        order.setOrderNo("SO" + orderId);
        order.setUserId(userId);
        order.setGeneralCode(generalCode);
        order.setStatus(SeckillOrderStatus.CREATED);
        seckillOrderMapper.insert(order);
        return order;
    }
}
