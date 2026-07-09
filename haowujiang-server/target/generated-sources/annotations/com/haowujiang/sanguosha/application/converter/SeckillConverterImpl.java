package com.haowujiang.sanguosha.application.converter;

import com.haowujiang.sanguosha.application.vo.seckill.response.SeckillEventRespVo;
import com.haowujiang.sanguosha.application.vo.seckill.response.SeckillOrderBasicVo;
import com.haowujiang.sanguosha.domain.model.SeckillEvent;
import com.haowujiang.sanguosha.infrastructure.persistence.po.SeckillOrder;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-10T01:47:01+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class SeckillConverterImpl implements SeckillConverter {

    @Override
    public SeckillOrderBasicVo orderToVo(SeckillOrder order) {
        if ( order == null ) {
            return null;
        }

        SeckillOrderBasicVo seckillOrderBasicVo = new SeckillOrderBasicVo();

        seckillOrderBasicVo.setGeneralId( order.getGeneralCode() );
        seckillOrderBasicVo.setUserId( order.getUserId() );

        seckillOrderBasicVo.setId( valueOf(order.getId()) );
        seckillOrderBasicVo.setStatus( order.getStatus() == null ? "" : order.getStatus().name().toLowerCase() );
        seckillOrderBasicVo.setCreatedAt( format(order.getCreateTime()) );

        return seckillOrderBasicVo;
    }

    @Override
    public SeckillEventRespVo eventToVo(SeckillEvent event) {
        if ( event == null ) {
            return null;
        }

        SeckillEventRespVo seckillEventRespVo = new SeckillEventRespVo();

        seckillEventRespVo.setId( event.getId() );
        seckillEventRespVo.setLevel( event.getLevel() );
        seckillEventRespVo.setMessage( event.getMessage() );

        seckillEventRespVo.setCreatedAt( format(event.getCreatedAt()) );

        return seckillEventRespVo;
    }
}
