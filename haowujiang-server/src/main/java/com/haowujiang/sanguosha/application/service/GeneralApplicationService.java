package com.haowujiang.sanguosha.application.service;

import com.haowujiang.sanguosha.infrastructure.common.PageResult;
import com.haowujiang.sanguosha.interfaces.vo.general.request.GeneralPageQueryReqVo;
import com.haowujiang.sanguosha.interfaces.vo.general.request.UserGeneralPageQueryReqVo;
import com.haowujiang.sanguosha.interfaces.vo.general.response.GeneralBasicVo;
import com.haowujiang.sanguosha.interfaces.vo.general.response.GeneralDetailVo;
import com.haowujiang.sanguosha.interfaces.vo.general.response.MyGeneralRespVo;

public interface GeneralApplicationService {

    PageResult<GeneralBasicVo> listGenerals(GeneralPageQueryReqVo query);

    GeneralDetailVo getGeneralInfo(Long id);

    PageResult<MyGeneralRespVo> listOwnedGenerals(Long userId, UserGeneralPageQueryReqVo query);

    MyGeneralRespVo grantGeneral(Long userId, Long generalId);
}
