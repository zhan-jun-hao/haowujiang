package com.haowujiang.sanguosha.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventSource {

    SECKILL_SERVICE("秒杀服务"),;

    @EnumValue
    private final String desc;
}
