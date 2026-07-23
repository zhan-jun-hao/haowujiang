package com.haowujiang.sanguosha.interfaces.vo.user.request;

import com.haowujiang.sanguosha.infrastructure.common.PageQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserPageQueryReqVo extends PageQuery {

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户状态：0禁用 1正常
     */
    private Integer status;

    /**
     * 用户角色：0普通用户 1管理员
     */
    private Integer role;
}
