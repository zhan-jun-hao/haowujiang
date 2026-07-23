package com.haowujiang.sanguosha.application.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haowujiang.sanguosha.application.converter.UserConverter;
import com.haowujiang.sanguosha.application.service.UserApplicationService;
import com.haowujiang.sanguosha.infrastructure.common.PageResult;
import com.haowujiang.sanguosha.infrastructure.persistence.po.User;
import com.haowujiang.sanguosha.interfaces.vo.user.request.AdminUserPageQueryReqVo;
import com.haowujiang.sanguosha.interfaces.vo.user.response.AdminUserBasicVo;
import com.haowujiang.sanguosha.domain.service.UserDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserApplicationServiceImpl implements UserApplicationService {

    private final UserDomainService userDomainService;

    @Override
    public PageResult<AdminUserBasicVo> getPageListUsers(AdminUserPageQueryReqVo query) {
        IPage<User> page = userDomainService.pageQuery(query);
        List<AdminUserBasicVo> records = UserConverter.INSTANCE.poToBasicVo(page.getRecords());
        return PageResult.success(page, records);
    }

    @Override
    public AdminUserBasicVo getUserInfo(Long userId) {
        return UserConverter.INSTANCE.poToBasicVo(userDomainService.getNormalUserById(userId));
    }
}
