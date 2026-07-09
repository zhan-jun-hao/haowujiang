package com.haowujiang.sanguosha.infrastructure.security.context;

public interface AuthenticatedUser {

    Long getUserId();

    Integer getRole();
}
