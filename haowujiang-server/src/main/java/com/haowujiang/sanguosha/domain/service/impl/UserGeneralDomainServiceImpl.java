package com.haowujiang.sanguosha.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haowujiang.sanguosha.domain.service.UserGeneralDomainService;
import com.haowujiang.sanguosha.infrastructure.persistence.mapper.UserGeneralMapper;
import com.haowujiang.sanguosha.infrastructure.persistence.po.UserGeneral;
import com.haowujiang.sanguosha.interfaces.vo.general.request.UserGeneralPageQueryReqVo;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserGeneralDomainServiceImpl implements UserGeneralDomainService {

    private final UserGeneralMapper userGeneralMapper;

    @Override
    public List<UserGeneral> findListByUserId(Long userId) {
        LambdaQueryWrapper<UserGeneral> wrapper = Wrappers.lambdaQuery(UserGeneral.class)
                .eq(UserGeneral::getDeleted, 0)
                .eq(UserGeneral::getUserId, userId);

        return userGeneralMapper.selectList(wrapper);
    }

    @Override
    public boolean existsByUserIdAndGeneralCode(Long userId, String generalCode) {
        LambdaQueryWrapper<UserGeneral> wrapper = Wrappers.lambdaQuery(UserGeneral.class)
                .eq(UserGeneral::getDeleted, 0)
                .eq(UserGeneral::getUserId, userId)
                .eq(UserGeneral::getGeneralCode, generalCode);
        return userGeneralMapper.selectCount(wrapper) > 0;
    }

    @Override
    public IPage<UserGeneral> pageQuery(Long userId, UserGeneralPageQueryReqVo query) {
        LambdaQueryWrapper<UserGeneral> wrapper = Wrappers.lambdaQuery(UserGeneral.class)
                .eq(UserGeneral::getDeleted, 0)
                .eq(UserGeneral::getUserId, userId)
                .orderByDesc(UserGeneral::getCreateTime);
        return userGeneralMapper.selectPage(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public UserGeneral grantGeneral(Long userId, String generalCode) {
        if (existsByUserIdAndGeneralCode(userId, generalCode)) {
            return userGeneralMapper.selectOne(Wrappers.lambdaQuery(UserGeneral.class)
                    .eq(UserGeneral::getDeleted, 0)
                    .eq(UserGeneral::getUserId, userId)
                    .eq(UserGeneral::getGeneralCode, generalCode));
        }
        UserGeneral userGeneral = new UserGeneral();
        userGeneral.setUserId(userId);
        userGeneral.setGeneralCode(generalCode);
        userGeneral.setObtainedAt(LocalDateTime.now());
        userGeneralMapper.insert(userGeneral);
        return userGeneral;
    }
}
