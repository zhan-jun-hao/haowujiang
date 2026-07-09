package com.haowujiang.sanguosha.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 武将获取来源枚举
 */
@Getter
@RequiredArgsConstructor
public enum GeneralUnlockSource {

    /**
     * 默认拥有。
     */
    DEFAULT(0, "默认拥有"),

    /**
     * 秒杀获取。
     */
    SECKILL(1, "秒杀获取");

    @EnumValue
    private final Integer code;

    private final String desc;
}
