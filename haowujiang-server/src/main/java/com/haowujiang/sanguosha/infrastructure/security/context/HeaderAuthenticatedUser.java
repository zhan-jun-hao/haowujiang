package com.haowujiang.sanguosha.infrastructure.security.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 已认证用户信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeaderAuthenticatedUser implements AuthenticatedUser {

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 用户角色
     */
    private Integer role;

    /**
     * traceId
     */
    private String traceId;

}
