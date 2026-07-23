package com.haowujiang.sanguosha.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haowujiang.sanguosha.domain.exception.BusinessException;
import com.haowujiang.sanguosha.domain.service.UserDomainService;
import com.haowujiang.sanguosha.infrastructure.persistence.mapper.UserMapper;
import com.haowujiang.sanguosha.infrastructure.persistence.po.User;
import java.util.List;
import java.util.Objects;

import com.haowujiang.sanguosha.interfaces.vo.user.request.AdminUserPageQueryReqVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 用户领域服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserDomainServiceImpl extends ServiceImpl<UserMapper, User> implements UserDomainService {

    private final UserMapper userMapper;

    @Override
    public User getNormalUserById(Long id) {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(User.class)
                .eq(User::getDeleted, 0)
                .eq(User::getStatus, 1)
                .eq(User::getId, id);
        User user = userMapper.selectOne(wrapper);
        if (Objects.isNull(user)) {
            throw new BusinessException("用户不存在或已被禁用");
        }
        return user;
    }

    @Override
    public List<User> listUsers() {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(User.class)
                .eq(User::getDeleted, 0)
                .orderByDesc(User::getId);
        return userMapper.selectList(wrapper);
    }

    @Override
    public IPage<User> pageQuery(AdminUserPageQueryReqVo pageQuery) {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(User.class)
                .eq(User::getDeleted, 0)
                .like(!StrUtil.isBlank(pageQuery.getNickname()), User::getNickname, pageQuery.getNickname())
                .eq(Objects.nonNull(pageQuery.getStatus()), User::getStatus, pageQuery.getStatus())
                .like(!StrUtil.isBlank(pageQuery.getPhone()), User::getPhone, pageQuery.getPhone())
                .eq(Objects.nonNull(pageQuery.getRole()), User::getRole, pageQuery.getRole())
                .orderByDesc(User::getCreateTime);
        return this.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), wrapper);
    }

    @Override
    public User findExistUserByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(User.class)
                .eq(User::getDeleted, 0)
                .eq(User::getPhone, phone);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public void saveUser(User user) {
        userMapper.insert(user);
    }
}
