package com.haowujiang.sanguosha.interfaces.vo.auth.response;

import com.haowujiang.sanguosha.interfaces.vo.user.response.AdminUserBasicVo;
import lombok.Data;

/**
 * 用户登录响应对象
 */
@Data
public class UserLoginRespVo {

    /**
     * JWT 访问令牌
     */
    private String token;

    /**
     * 用户基础信息
     */
    private AdminUserBasicVo adminUserBasicVo;
}
