package com.haowujiang.sanguosha.application.converter;

import com.haowujiang.sanguosha.application.vo.general.response.GeneralBasicVo;
import com.haowujiang.sanguosha.infrastructure.persistence.po.General;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-10T01:47:01+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class GeneralConverterImpl implements GeneralConverter {

    @Override
    public GeneralBasicVo poToBasicVo(General po) {
        if ( po == null ) {
            return null;
        }

        GeneralBasicVo generalBasicVo = new GeneralBasicVo();

        generalBasicVo.setId( po.getId() );
        generalBasicVo.setCode( po.getCode() );
        generalBasicVo.setName( po.getName() );
        generalBasicVo.setTitle( po.getTitle() );
        generalBasicVo.setCamp( po.getCamp() );
        generalBasicVo.setHp( po.getHp() );
        generalBasicVo.setRarity( po.getRarity() );
        generalBasicVo.setSkillName( po.getSkillName() );
        generalBasicVo.setSkillSummary( po.getSkillSummary() );
        generalBasicVo.setUnlockSource( po.getUnlockSource() );

        return generalBasicVo;
    }

    @Override
    public List<GeneralBasicVo> poToBasicVo(List<General> po) {
        if ( po == null ) {
            return null;
        }

        List<GeneralBasicVo> list = new ArrayList<GeneralBasicVo>( po.size() );
        for ( General general : po ) {
            list.add( poToBasicVo( general ) );
        }

        return list;
    }
}
