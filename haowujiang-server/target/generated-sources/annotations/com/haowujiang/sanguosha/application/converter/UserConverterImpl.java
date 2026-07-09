package com.haowujiang.sanguosha.application.converter;

import com.haowujiang.sanguosha.application.vo.user.response.UserBasicVo;
import com.haowujiang.sanguosha.infrastructure.persistence.po.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-10T01:47:01+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class UserConverterImpl implements UserConverter {

    @Override
    public UserBasicVo poToBasicVo(User po) {
        if ( po == null ) {
            return null;
        }

        UserBasicVo userBasicVo = new UserBasicVo();

        userBasicVo.setId( po.getId() );
        userBasicVo.setPhone( po.getPhone() );
        userBasicVo.setNickname( po.getNickname() );
        userBasicVo.setRole( po.getRole() );
        userBasicVo.setStatus( po.getStatus() );

        return userBasicVo;
    }

    @Override
    public List<UserBasicVo> poToBasicVo(List<User> po) {
        if ( po == null ) {
            return null;
        }

        List<UserBasicVo> list = new ArrayList<UserBasicVo>( po.size() );
        for ( User user : po ) {
            list.add( poToBasicVo( user ) );
        }

        return list;
    }
}
