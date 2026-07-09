package com.haowujiang.sanguosha.application.converter;

import com.haowujiang.sanguosha.application.vo.seckill.response.SeckillEventRespVo;
import com.haowujiang.sanguosha.application.vo.seckill.response.SeckillOrderBasicVo;
import com.haowujiang.sanguosha.application.vo.seckill.response.SeckillResultRespVo;
import com.haowujiang.sanguosha.application.vo.seckill.response.SeckillStateRespVo;
import com.haowujiang.sanguosha.domain.model.SeckillEvent;
import com.haowujiang.sanguosha.domain.model.SeckillResult;
import com.haowujiang.sanguosha.domain.model.SeckillState;
import com.haowujiang.sanguosha.infrastructure.persistence.po.SeckillOrder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SeckillConverter {

    SeckillConverter INSTANCE = Mappers.getMapper(SeckillConverter.class);

    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    default SeckillStateRespVo modelToVo(SeckillState state) {
        if (state == null) {
            return null;
        }
        SeckillStateRespVo vo = new SeckillStateRespVo();
        vo.setTargetGeneralId(state.getTargetGeneralId());
        vo.setStock(state.getStock());
        vo.setTotal(state.getTotal());
        vo.setClaimedUserIds(new ArrayList<>(state.getClaimedUserIds()));
        vo.setOrders(state.getOrders().stream()
                .map(this::orderToVo)
                .collect(Collectors.toCollection(ArrayList::new)));
        vo.setEvents(state.getEvents().stream()
                .map(this::eventToVo)
                .collect(Collectors.toCollection(ArrayList::new)));
        return vo;
    }

    default SeckillResultRespVo modelToVo(SeckillResult result) {
        if (result == null) {
            return null;
        }
        SeckillResultRespVo vo = new SeckillResultRespVo();
        vo.setOk(result.isOk());
        vo.setTitle(result.getTitle());
        vo.setMessage(result.getMessage());
        vo.setSteps(new ArrayList<>(result.getSteps()));
        vo.setState(modelToVo(result.getState()));
        return vo;
    }

    @Mapping(target = "id", expression = "java(valueOf(order.getId()))")
    @Mapping(target = "generalId", source = "generalCode")
    @Mapping(target = "status", expression = "java(order.getStatus() == null ? \"\" : order.getStatus().name().toLowerCase())")
    @Mapping(target = "createdAt", expression = "java(format(order.getCreateTime()))")
    SeckillOrderBasicVo orderToVo(SeckillOrder order);

    @Mapping(target = "createdAt", expression = "java(format(event.getCreatedAt()))")
    SeckillEventRespVo eventToVo(SeckillEvent event);

    default String valueOf(Long value) {
        return value == null ? "" : String.valueOf(value);
    }

    default String format(LocalDateTime time) {
        return time == null ? "" : FORMATTER.format(time);
    }
}
