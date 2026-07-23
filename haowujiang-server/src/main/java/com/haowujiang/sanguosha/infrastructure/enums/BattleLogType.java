package com.haowujiang.sanguosha.infrastructure.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 对战日志类型
 */
@Getter
@RequiredArgsConstructor
public enum BattleLogType {

    SYSTEM("system", "系统"),
    PLAYER("player", "玩家"),
    ENEMY("enemy", "敌方"),
    AI("ai", "AI"),
    HEAL("heal", "回复"),
    DAMAGE("damage", "伤害");

    @JsonValue
    private final String value;

    private final String label;
}
