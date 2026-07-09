package com.haowujiang.sanguosha.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 秒杀订单创建事件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrderCreatedEvent {

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 武将编码
     */
    private String generalCode;
}
