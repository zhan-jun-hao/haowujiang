package com.haowujiang.sanguosha.application.service;

import com.haowujiang.sanguosha.application.vo.user.response.UserBasicVo;
import java.util.List;

public interface UserApplicationService {

    List<UserBasicVo> listUsers();

    UserBasicVo getUserInfo(Long userId);
}
