package com.haowujiang.sanguosha.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 对战卡牌类型枚举
 */
@Getter
@RequiredArgsConstructor
public enum CardKind {

    /**
     * 杀
     */
    SHA("杀"),

    /**
     * 闪
     */
    SHAN("闪"),

    /**
     * 桃
     */
    TAO("桃"),

    /**
     * 酒
     */
    JIU("酒");

    @EnumValue
    private final String label;

}
