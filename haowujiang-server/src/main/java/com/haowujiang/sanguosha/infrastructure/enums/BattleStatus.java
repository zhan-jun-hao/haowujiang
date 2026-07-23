package com.haowujiang.sanguosha.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 对战状态枚举
 */
@Getter
@RequiredArgsConstructor
public enum BattleStatus {

    /**
     * 对战进行中
     */
    PLAYING(0, "进行中"),

    /**
     * 对战已结束
     */
    FINISHED(1, "已结束");

    @EnumValue
    private final Integer code;

    private final String desc;
}
