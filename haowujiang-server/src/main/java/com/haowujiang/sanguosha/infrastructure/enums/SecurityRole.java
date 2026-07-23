package com.haowujiang.sanguosha.infrastructure.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户权限枚举
 */
@Getter
@AllArgsConstructor
public enum SecurityRole {

    USER(0, "ROLE_USER"),

    ADMIN(1, "ROLE_ADMIN");

    private final Integer code;

    private final String authority;

    public static SecurityRole of(Integer code) {
        for (SecurityRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        return null;
    }
}
