package com.haowujiang.sanguosha.infrastructure.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 对战待响应类型
 */
@Getter
@RequiredArgsConstructor
public enum PendingResponseType {

    SHA("SHA", "需要出闪"),
    JIEDAO("JIEDAO", "借刀杀人"),
    WUXIE("WUXIE", "无懈可击"),
    JUDGE("JUDGE", "判定"),
    DYING("DYING", "濒死");

    @JsonValue
    private final String value;

    private final String label;
}
