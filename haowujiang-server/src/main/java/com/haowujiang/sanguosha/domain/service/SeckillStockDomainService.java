package com.haowujiang.sanguosha.domain.service;

import com.haowujiang.sanguosha.infrastructure.persistence.po.SeckillStock;

/**
 * 秒杀库存领域服务接口
 */
public interface SeckillStockDomainService {

    /**
     * 根据武将编码查询库存
     *
     * @param generalCode 武将编码
     * @return 秒杀库存
     */
    SeckillStock getByGeneralCode(String generalCode);

    /**
     * 扣减库存
     *
     * @param generalCode 武将编码
     * @return 是否扣减成功
     */
    boolean deductStock(String generalCode);
}
