package com.haowujiang.sanguosha.application.service;

import com.haowujiang.sanguosha.application.vo.general.response.GeneralBasicVo;
import com.haowujiang.sanguosha.application.vo.general.response.GeneralDetailVo;
import com.haowujiang.sanguosha.application.vo.general.response.MyGeneralRespVo;
import java.util.List;

public interface GeneralApplicationService {

    List<GeneralBasicVo> listGenerals();

    GeneralDetailVo getGeneralInfo(Long id);

    List<MyGeneralRespVo> listOwnedGenerals(Long userId);

    MyGeneralRespVo grantGeneral(Long userId, Long generalId);
}
