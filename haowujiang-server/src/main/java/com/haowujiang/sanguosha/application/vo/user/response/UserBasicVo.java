package com.haowujiang.sanguosha.application.vo.user.response;

import lombok.Data;

/**
 * 用户基础视图对象，对应 user 表
 */
@Data
public class UserBasicVo {

    /**
     * 用户 ID
     */
    private Long id;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户角色：0 普通用户，1 管理员
     */
    private Integer role;

    /**
     * 用户状态：0 禁用，1 正常
     */
    private Integer status;
}
