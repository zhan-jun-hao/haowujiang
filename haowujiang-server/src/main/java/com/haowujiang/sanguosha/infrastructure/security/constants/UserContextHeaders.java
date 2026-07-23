package com.haowujiang.sanguosha.infrastructure.security.constants;

/**
 * 请求头常量类
 * Header名称
 */
public final class UserContextHeaders {

    public static final String USER_ID = "X-User-Id";

    public static final String USER_ROLE = "X-User-Role";

    public static final String TRACE_ID = "X-TRACE_Id";

    /**
     * 私有化构造方法 不让别人重复new出来
     */
    private UserContextHeaders() {
    }
}