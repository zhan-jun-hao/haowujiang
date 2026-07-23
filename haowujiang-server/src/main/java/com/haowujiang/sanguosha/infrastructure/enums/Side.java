package com.haowujiang.sanguosha.infrastructure.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 对战阵营
 */
@Getter
@RequiredArgsConstructor
public enum Side {

    PLAYER("player", "玩家"),
    ENEMY("enemy", "敌方");

    @JsonValue
    private final String value;

    private final String label;
}
