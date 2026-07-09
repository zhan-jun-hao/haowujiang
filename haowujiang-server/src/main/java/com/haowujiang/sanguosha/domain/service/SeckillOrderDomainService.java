package com.haowujiang.sanguosha.domain.service;

import com.haowujiang.sanguosha.infrastructure.persistence.po.SeckillOrder;
import java.util.List;

/**
 * 秒杀订单领域服务接口
 */
public interface SeckillOrderDomainService {

    /**
     * 查询武将秒杀订单列表
     *
     * @param generalCode 武将编码
     * @return 秒杀订单列表
     */
    List<SeckillOrder> listByGeneralCode(String generalCode);

    /**
     * 判断用户是否已经创建过订单
     *
     * @param userId 用户 ID
     * @param generalCode 武将编码
     * @return 是否存在订单
     */
    boolean existsByUserIdAndGeneralCode(Long userId, String generalCode);

    /**
     * 创建秒杀订单
     * @param orderId
     * @param userId
     * @param generalCode
     * @return
     */
    SeckillOrder createOrder(Long orderId, Long userId, String generalCode);
}
