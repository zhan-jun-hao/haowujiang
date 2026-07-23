package com.haowujiang.sanguosha.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 事件类型枚举
 */
@Getter
@RequiredArgsConstructor
public enum EventType {

    SECKILL_SUCCESS("seckill_success", "秒杀成功"),
    SECKILL_FAIL("seckill_fail", "秒杀失败"),
    SECKILL_OVER("seckill_over", "秒杀结束"),

    ORDER_CREATED("order_created", "订单创建"),
    ORDER_PAID("order_paid", "订单支付"),
    ORDER_CANCEL("order_cancel", "订单取消"),
    ORDER_REFUND("order_refund", "订单退款"),
    ORDER_COMPLETE("order_complete", "订单完成"),

    STOCK_REDUCE("stock_reduce", "库存扣减"),
    STOCK_RESTORE("stock_restore", "库存恢复"),
    STOCK_WARN("stock_warn", "库存预警");

    /**
     * 事件编码（用于消息传输和数据库存储）
     */
    @EnumValue
    private final String code;

    /**
     * 事件描述
     */
    private final String desc;

    /**
     * 根据 code 获取枚举
     */
    public static EventType of(String code) {
        for (EventType type : EventType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知事件类型: " + code);
    }

}