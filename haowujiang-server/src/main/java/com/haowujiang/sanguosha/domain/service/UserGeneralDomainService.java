package com.haowujiang.sanguosha.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haowujiang.sanguosha.infrastructure.persistence.po.UserGeneral;
import com.haowujiang.sanguosha.interfaces.vo.general.request.UserGeneralPageQueryReqVo;
import java.util.List;

public interface UserGeneralDomainService {

    List<UserGeneral> findListByUserId(Long userId);

    boolean existsByUserIdAndGeneralCode(Long userId, String generalCode);

    UserGeneral grantGeneral(Long userId, String generalCode);

    IPage<UserGeneral> pageQuery(Long userId, UserGeneralPageQueryReqVo query);
}
