package com.haowujiang.sanguosha.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.haowujiang.sanguosha.domain.service.SeckillStockDomainService;
import com.haowujiang.sanguosha.infrastructure.persistence.mapper.SeckillStockMapper;
import com.haowujiang.sanguosha.infrastructure.persistence.po.SeckillStock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 秒杀库存领域服务实现类
 */
@Service
@RequiredArgsConstructor
public class SeckillStockDomainServiceImpl implements SeckillStockDomainService {

    private final SeckillStockMapper seckillStockMapper;

    @Override
    public SeckillStock getByGeneralCode(String generalCode) {
        LambdaQueryWrapper<SeckillStock> wrapper = Wrappers.lambdaQuery(SeckillStock.class)
                .eq(SeckillStock::getDeleted, 0)
                .eq(SeckillStock::getGeneralCode, generalCode);
        return seckillStockMapper.selectOne(wrapper);
    }

    @Override
    public boolean deductStock(String generalCode) {
        return seckillStockMapper.update(Wrappers.lambdaUpdate(SeckillStock.class)
                .setSql("availableStock = availableStock - 1")
                .eq(SeckillStock::getDeleted, 0)
                .eq(SeckillStock::getGeneralCode, generalCode)
                .gt(SeckillStock::getAvailableStock, 0)) > 0;
    }
}
