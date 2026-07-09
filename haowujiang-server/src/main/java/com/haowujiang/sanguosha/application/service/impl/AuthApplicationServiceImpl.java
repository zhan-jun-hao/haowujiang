package com.haowujiang.sanguosha.application.service.impl;

import com.haowujiang.sanguosha.application.converter.UserConverter;
import com.haowujiang.sanguosha.application.service.AuthApplicationService;
import com.haowujiang.sanguosha.application.vo.auth.request.UserLoginReqVo;
import com.haowujiang.sanguosha.application.vo.auth.response.UserLoginRespVo;
import com.haowujiang.sanguosha.domain.exception.BusinessException;
import com.haowujiang.sanguosha.domain.service.UserDomainService;
import com.haowujiang.sanguosha.infrastructure.persistence.po.User;
import com.haowujiang.sanguosha.infrastructure.security.jwt.JwtTokenProvider;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthApplicationServiceImpl implements AuthApplicationService {

    private final UserDomainService userDomainService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UserLoginRespVo login(UserLoginReqVo reqVo) {
        User user = userDomainService.findExistUserByPhone(reqVo.getPhone());
        if (Objects.isNull(user) || !passwordMatches(reqVo.getPassword(), user.getPassword())) {
            throw new BusinessException("手机号或密码错误");
        }
        if (!Objects.equals(user.getStatus(), 1)) {
            throw new BusinessException("用户已被禁用");
        }

        UserLoginRespVo respVo = new UserLoginRespVo();
        respVo.setToken(jwtTokenProvider.createToken(user.getId(), user.getRole()));
        respVo.setUserBasicVo(UserConverter.INSTANCE.poToBasicVo(user));
        return respVo;
    }

    private boolean passwordMatches(String rawPassword, String encodedPassword) {
        if (!StringUtils.hasText(encodedPassword)) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
