package com.haowujiang.sanguosha.domain.service;

import com.haowujiang.sanguosha.infrastructure.persistence.po.UserGeneral;

import java.util.List;

public interface UserGeneralDomainService {

    List<UserGeneral> findListByUserId(Long userId);

    boolean existsByUserIdAndGeneralCode(Long userId, String generalCode);

    UserGeneral grantGeneral(Long userId, String generalCode);
}
