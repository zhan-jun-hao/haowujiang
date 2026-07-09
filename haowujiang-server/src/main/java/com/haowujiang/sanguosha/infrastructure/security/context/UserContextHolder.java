package com.haowujiang.sanguosha.infrastructure.security.context;

import com.haowujiang.sanguosha.domain.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 用户上下文容器
 */
public final class UserContextHolder {

    private UserContextHolder() {
    }

    public static AuthenticatedUser getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            return null;
        }
        return user;
    }

    public static Long getUserId() {
        AuthenticatedUser user = getUser();
        return user == null ? null : user.getUserId();
    }

    public static Long getRequiredUserId() {
        Long userId = getUserId();
        if (userId == null) {
            throw new BusinessException("User is not logged in");
        }
        return userId;
    }

    public static Integer getRole() {
        AuthenticatedUser user = getUser();
        return user == null ? null : user.getRole();
    }
}
