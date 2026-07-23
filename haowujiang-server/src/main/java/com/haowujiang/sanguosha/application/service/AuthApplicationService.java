package com.haowujiang.sanguosha.application.service;

import com.haowujiang.sanguosha.interfaces.vo.auth.request.UserLoginReqVo;
import com.haowujiang.sanguosha.interfaces.vo.auth.response.UserLoginRespVo;

public interface AuthApplicationService {

    UserLoginRespVo login(UserLoginReqVo reqVo);
}
