package com.haowujiang.sanguosha.application.service.impl;

import com.haowujiang.sanguosha.application.converter.GeneralConverter;
import com.haowujiang.sanguosha.application.service.GeneralApplicationService;
import com.haowujiang.sanguosha.application.vo.general.response.GeneralBasicVo;
import com.haowujiang.sanguosha.application.vo.general.response.GeneralDetailVo;
import com.haowujiang.sanguosha.application.vo.general.response.MyGeneralRespVo;
import com.haowujiang.sanguosha.domain.service.GeneralDomainService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.haowujiang.sanguosha.domain.service.UserGeneralDomainService;
import com.haowujiang.sanguosha.infrastructure.persistence.po.General;
import com.haowujiang.sanguosha.infrastructure.persistence.po.UserGeneral;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeneralApplicationServiceImpl implements GeneralApplicationService {

    private final GeneralDomainService generalDomainService;
    private final UserGeneralDomainService userGeneralDomainService;

    @Override
    public List<GeneralBasicVo> listGenerals() {
        return GeneralConverter.INSTANCE.poToBasicVo(generalDomainService.listGenerals());
    }

    @Override
    public GeneralDetailVo getGeneralInfo(Long id) {
        return GeneralConverter.INSTANCE.poToDetailVo(generalDomainService.getGeneralById(id));
    }

    @Override
    public List<MyGeneralRespVo> listOwnedGenerals(Long userId) {
        List<UserGeneral> userGenerals = userGeneralDomainService.findListByUserId(userId);
        if (userGenerals.isEmpty()) {
            return new ArrayList<>();
        }
        Set<String> codes = userGenerals.stream().map(UserGeneral::getGeneralCode).collect(Collectors.toSet());

        return generalDomainService.listOwnedGeneralsByCode(codes)
                .stream()
                .map(GeneralConverter.INSTANCE::poToMyGeneralRespVo)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public MyGeneralRespVo grantGeneral(Long userId, Long generalId) {
        General general = generalDomainService.getGeneralById(generalId);
        userGeneralDomainService.grantGeneral(userId, general.getCode());
        return GeneralConverter.INSTANCE.poToMyGeneralRespVo(general);
    }
}
