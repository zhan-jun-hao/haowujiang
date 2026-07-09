package com.haowujiang.sanguosha.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户持久化对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseEntity {

    /**
     * 手机号。
     */
    private String phone;

    /**
     * 用户昵称。
     */
    private String nickname;

    /**
     * 密码（加密存储）。
     */
    private String password;

    /**
     * 用户角色：
     * 0 普通用户
     * 1 管理员
     */
    private Integer role;

    /**
     * 用户状态：
     * 0 禁用
     * 1 正常
     */
    private Integer status;
}
