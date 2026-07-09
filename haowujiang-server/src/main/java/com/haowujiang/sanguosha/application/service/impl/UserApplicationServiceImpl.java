package com.haowujiang.sanguosha.application.service.impl;

import com.haowujiang.sanguosha.application.converter.UserConverter;
import com.haowujiang.sanguosha.application.service.UserApplicationService;
import com.haowujiang.sanguosha.application.vo.user.response.UserBasicVo;
import com.haowujiang.sanguosha.domain.service.UserDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserApplicationServiceImpl implements UserApplicationService {

    private final UserDomainService userDomainService;

    @Override
    public List<UserBasicVo> listUsers() {
        return UserConverter.INSTANCE.poToBasicVo(userDomainService.listUsers());
    }

    @Override
    public UserBasicVo getUserInfo(Long userId) {
        return UserConverter.INSTANCE.poToBasicVo(userDomainService.getNormalUserById(userId));
    }
}
