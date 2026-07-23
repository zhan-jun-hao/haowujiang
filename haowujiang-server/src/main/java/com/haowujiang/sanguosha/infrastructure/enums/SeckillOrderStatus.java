package com.haowujiang.sanguosha.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 秒杀订单状态枚举
 */
@Getter
@RequiredArgsConstructor
public enum SeckillOrderStatus {

    /**
     * 已创建
     */
    CREATED(0, "已创建"),

    /**
     * 队列中
     */
    QUEUED(1, "队列中"),

    /**
     * 失败
     */
    FAILED(2, "失败");

    @EnumValue
    private final Integer code;

    private final String desc;
}
