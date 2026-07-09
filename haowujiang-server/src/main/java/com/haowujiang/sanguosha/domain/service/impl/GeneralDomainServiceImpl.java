package com.haowujiang.sanguosha.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.haowujiang.sanguosha.domain.exception.BusinessException;
import com.haowujiang.sanguosha.domain.service.GeneralDomainService;
import com.haowujiang.sanguosha.infrastructure.persistence.mapper.GeneralMapper;
import com.haowujiang.sanguosha.infrastructure.persistence.po.General;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeneralDomainServiceImpl implements GeneralDomainService {

    private final GeneralMapper generalMapper;

    @Override
    public List<General> listGenerals() {
        LambdaQueryWrapper<General> wrapper = Wrappers.lambdaQuery(General.class)
                .eq(General::getDeleted, 0);
        return new ArrayList<>(generalMapper.selectList(wrapper));
    }

    @Override
    public General getGeneralById(Long id) {
        LambdaQueryWrapper<General> wrapper = Wrappers.lambdaQuery(General.class)
                .eq(General::getDeleted, 0)
                .eq(General::getId, id);
        General general = generalMapper.selectOne(wrapper);
        if (Objects.isNull(general)) {
            throw new BusinessException("武将不存在或已下架!");
        }
        return general;
    }

    @Override
    public General getGeneralByCode(String code) {
        LambdaQueryWrapper<General> wrapper = Wrappers.lambdaQuery(General.class)
                .eq(General::getDeleted, 0)
                .eq(General::getCode, code);
        General general = generalMapper.selectOne(wrapper);
        if (Objects.isNull(general)) {
            throw new BusinessException("武将不存在或已下架");
        }
        return general;
    }

    @Override
    public List<General> listOwnedGeneralsByCode(Set<String> code) {
        if (code == null || code.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<General> wrapper = Wrappers.lambdaQuery(General.class)
                .eq(General::getDeleted, 0)
                .in(General::getCode, code);

        return new ArrayList<>(generalMapper.selectList(wrapper));
    }
}
