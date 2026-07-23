package com.haowujiang.sanguosha.application.converter;

import com.haowujiang.sanguosha.infrastructure.persistence.po.SeckillActivity;
import com.haowujiang.sanguosha.interfaces.vo.seckill.response.SeckillActivityRespVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SeckillConverter {

    SeckillConverter INSTANCE = Mappers.getMapper(SeckillConverter.class);

    /**
     * 秒杀活动 PO → VO
     */
    default SeckillActivityRespVo activityToVo(SeckillActivity activity) {
        if (activity == null) {
            return null;
        }
        SeckillActivityRespVo vo = new SeckillActivityRespVo();
        vo.setId(activity.getId());
        vo.setActivityCode(activity.getActivityCode());
        vo.setActivityName(activity.getActivityName());
        vo.setGeneralCode(activity.getGeneralCode());
        vo.setGeneralName(activity.getGeneralName());
        vo.setStock(activity.getStock());
        vo.setAvailableStock(activity.getAvailableStock());
        vo.setLimitPerUser(activity.getLimitPerUser());
        vo.setStartTime(activity.getStartTime());
        vo.setEndTime(activity.getEndTime());
        vo.setStatus(activity.getStatus());
        vo.setStatusText(statusText(activity.getStatus()));
        vo.setCreatedBy(activity.getCreatedBy());
        vo.setCreatedTime(activity.getCreatedTime());
        vo.setUpdatedTime(activity.getUpdatedTime());
        return vo;
    }

    default String statusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case 0 -> "待开始";
            case 1 -> "进行中";
            case 2 -> "已结束";
            case 3 -> "已下架";
            default -> "未知";
        };
    }
}
