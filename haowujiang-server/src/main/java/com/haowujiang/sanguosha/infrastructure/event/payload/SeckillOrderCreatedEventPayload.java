package com.haowujiang.sanguosha.infrastructure.event.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 秒杀订单创建事件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrderCreatedEventPayload {

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
