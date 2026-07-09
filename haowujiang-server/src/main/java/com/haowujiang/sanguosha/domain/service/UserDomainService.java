package com.haowujiang.sanguosha.domain.service;

import com.haowujiang.sanguosha.infrastructure.persistence.po.User;
import java.util.List;

/**
 * 用户领域服务接口
 */
public interface UserDomainService {

    /**
     * 根据用户 ID 查询正常用户
     *
     * @param id 用户 ID
     * @return 用户持久化对象
     */
    User getNormalUserById(Long id);

    /**
     * 查询未删除用户列表
     *
     * @return 用户持久化对象列表
     */
    List<User> listUsers();

    /**
     * 根据手机号查询未删除用户
     *
     * @param phone 手机号
     * @return 用户持久化对象
     */
    User findExistUserByPhone(String phone);

    /**
     * 保存用户
     *
     * @param user 用户持久化对象
     */
    void saveUser(User user);
}
