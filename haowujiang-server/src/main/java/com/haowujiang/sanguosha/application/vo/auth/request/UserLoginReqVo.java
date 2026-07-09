package com.haowujiang.sanguosha.application.vo.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录请求对象
 */
@Data
public class UserLoginReqVo {

    /**
     * 手机号
     */
    @NotBlank
    private String phone;

    /**
     * 密码
     */
    @NotBlank
    private String password;
}
