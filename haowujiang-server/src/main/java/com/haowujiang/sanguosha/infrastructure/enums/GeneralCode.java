package com.haowujiang.sanguosha.infrastructure.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GeneralCode {

    ZHAO_YUN("zhao-yun"),

    ZHANG_FEI("zhang-fei");

    @EnumValue
    private final String value;

}


