package com.haowujiang.sanguosha.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haowujiang.sanguosha.domain.exception.BusinessException;
import com.haowujiang.sanguosha.domain.service.GeneralDomainService;
import com.haowujiang.sanguosha.infrastructure.persistence.mapper.GeneralMapper;
import com.haowujiang.sanguosha.infrastructure.persistence.po.General;
import com.haowujiang.sanguosha.interfaces.vo.general.request.GeneralPageQueryReqVo;
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
    public IPage<General> pageQuery(GeneralPageQueryReqVo query) {
        LambdaQueryWrapper<General> wrapper = Wrappers.lambdaQuery(General.class)
                .eq(General::getDeleted, 0)
                .like(StrUtil.isNotBlank(query.getName()), General::getName, query.getName())
                .eq(StrUtil.isNotBlank(query.getCamp()), General::getCamp, query.getCamp())
                .orderByDesc(General::getCreateTime);
        return generalMapper.selectPage(new Page<>(query.getCurrent(), query.getSize()), wrapper);
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
