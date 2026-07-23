package com.haowujiang.sanguosha.application.service;

import com.haowujiang.sanguosha.infrastructure.common.PageResult;
import com.haowujiang.sanguosha.interfaces.vo.user.request.AdminUserPageQueryReqVo;
import com.haowujiang.sanguosha.interfaces.vo.user.response.AdminUserBasicVo;
import java.util.List;

public interface UserApplicationService {

    /**
     * 分页查询用户
     * @param query
     * @return
     */
    PageResult<AdminUserBasicVo> getPageListUsers(AdminUserPageQueryReqVo query);

    AdminUserBasicVo getUserInfo(Long userId);
}
