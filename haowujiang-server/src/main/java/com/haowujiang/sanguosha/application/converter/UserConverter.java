package com.haowujiang.sanguosha.application.converter;

import com.haowujiang.sanguosha.application.vo.user.response.UserBasicVo;
import com.haowujiang.sanguosha.infrastructure.persistence.po.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserConverter {

    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    UserBasicVo poToBasicVo(User po);

    List<UserBasicVo> poToBasicVo(List<User> po);
}
