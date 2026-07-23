package com.haowujiang.sanguosha.application.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haowujiang.sanguosha.application.converter.GeneralConverter;
import com.haowujiang.sanguosha.application.service.GeneralApplicationService;
import com.haowujiang.sanguosha.infrastructure.common.PageResult;
import com.haowujiang.sanguosha.interfaces.vo.general.request.GeneralPageQueryReqVo;
import com.haowujiang.sanguosha.interfaces.vo.general.request.UserGeneralPageQueryReqVo;
import com.haowujiang.sanguosha.interfaces.vo.general.response.GeneralBasicVo;
import com.haowujiang.sanguosha.interfaces.vo.general.response.GeneralDetailVo;
import com.haowujiang.sanguosha.interfaces.vo.general.response.MyGeneralRespVo;
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
    public PageResult<GeneralBasicVo> listGenerals(GeneralPageQueryReqVo query) {
        IPage<General> page = generalDomainService.pageQuery(query);
        List<GeneralBasicVo> records = GeneralConverter.INSTANCE.poToBasicVo(page.getRecords());
        return PageResult.success(page, records);
    }

    @Override
    public GeneralDetailVo getGeneralInfo(Long id) {
        return GeneralConverter.INSTANCE.poToDetailVo(generalDomainService.getGeneralById(id));
    }

    @Override
    public PageResult<MyGeneralRespVo> listOwnedGenerals(Long userId, UserGeneralPageQueryReqVo query) {
        IPage<UserGeneral> userGeneralPage = userGeneralDomainService.pageQuery(userId, query);
        List<UserGeneral> userGenerals = userGeneralPage.getRecords();
        if (userGenerals.isEmpty()) {
            return PageResult.success(userGeneralPage, new ArrayList<>());
        }
        Set<String> codes = userGenerals.stream().map(UserGeneral::getGeneralCode).collect(Collectors.toSet());

        List<MyGeneralRespVo> records = generalDomainService.listOwnedGeneralsByCode(codes)
                .stream()
                .map(GeneralConverter.INSTANCE::poToMyGeneralRespVo)
                .collect(Collectors.toCollection(ArrayList::new));
        return PageResult.success(userGeneralPage, records);
    }

    @Override
    public MyGeneralRespVo grantGeneral(Long userId, Long generalId) {
        General general = generalDomainService.getGeneralById(generalId);
        userGeneralDomainService.grantGeneral(userId, general.getCode());
        return GeneralConverter.INSTANCE.poToMyGeneralRespVo(general);
    }
}
