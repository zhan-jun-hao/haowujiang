package com.haowujiang.sanguosha.application.converter;

import com.haowujiang.sanguosha.application.vo.general.response.GeneralBasicVo;
import com.haowujiang.sanguosha.application.vo.general.response.GeneralDetailVo;
import com.haowujiang.sanguosha.application.vo.general.response.MyGeneralRespVo;
import com.haowujiang.sanguosha.infrastructure.persistence.po.General;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GeneralConverter {

    GeneralConverter INSTANCE = Mappers.getMapper(GeneralConverter.class);

    GeneralBasicVo poToBasicVo(General po);

    List<GeneralBasicVo> poToBasicVo(List<General> po);

    default GeneralDetailVo poToDetailVo(General po) {
        if (po == null) {
            return null;
        }
        GeneralDetailVo detailVo = new GeneralDetailVo();
        detailVo.setBasicVo(poToBasicVo(po));
        detailVo.setCreateTime(po.getCreateTime());
        detailVo.setUpdateTime(po.getUpdateTime());
        detailVo.setDeleted(po.getDeleted());
        return detailVo;
    }

    default MyGeneralRespVo poToMyGeneralRespVo(General po) {
        if (po == null) {
            return null;
        }
        MyGeneralRespVo respVo = new MyGeneralRespVo();
        respVo.setGeneralBasicVo(poToBasicVo(po));
        return respVo;
    }

}
