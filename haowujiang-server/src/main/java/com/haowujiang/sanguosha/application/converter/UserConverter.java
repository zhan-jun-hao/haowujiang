package com.haowujiang.sanguosha.application.converter;

import com.haowujiang.sanguosha.interfaces.vo.user.response.AdminUserBasicVo;
import com.haowujiang.sanguosha.infrastructure.persistence.po.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserConverter {

    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    AdminUserBasicVo poToBasicVo(User po);

    List<AdminUserBasicVo> poToBasicVo(List<User> po);
}
