package com.haowujiang.sanguosha.application.converter;

import com.haowujiang.sanguosha.infrastructure.persistence.po.User;
import com.haowujiang.sanguosha.interfaces.vo.user.response.AdminUserBasicVo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-21T20:40:17+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class UserConverterImpl implements UserConverter {

    @Override
    public AdminUserBasicVo poToBasicVo(User po) {
        if ( po == null ) {
            return null;
        }

        AdminUserBasicVo adminUserBasicVo = new AdminUserBasicVo();

        adminUserBasicVo.setId( po.getId() );
        adminUserBasicVo.setPhone( po.getPhone() );
        adminUserBasicVo.setNickname( po.getNickname() );
        adminUserBasicVo.setRole( po.getRole() );
        adminUserBasicVo.setStatus( po.getStatus() );

        return adminUserBasicVo;
    }

    @Override
    public List<AdminUserBasicVo> poToBasicVo(List<User> po) {
        if ( po == null ) {
            return null;
        }

        List<AdminUserBasicVo> list = new ArrayList<AdminUserBasicVo>( po.size() );
        for ( User user : po ) {
            list.add( poToBasicVo( user ) );
        }

        return list;
    }
}
