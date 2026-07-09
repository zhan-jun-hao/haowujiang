package com.haowujiang.sanguosha.application.vo.seckill.response;

import java.util.List;
import lombok.Data;

/**
 * 秒杀状态响应对象
 */
@Data
public class SeckillStateRespVo {

    /**
     * 目标武将编码
     */
    private String targetGeneralId;

    /**
     * 剩余库存
     */
    private Integer stock;

    /**
     * 总库存
     */
    private Integer total;

    /**
     * 已抢购用户 ID 列表
     */
    private List<Long> claimedUserIds;

    /**
     * 秒杀订单基础信息列表
     */
    private List<SeckillOrderBasicVo> orders;

    /**
     * 秒杀事件列表
     */
    private List<SeckillEventRespVo> events;
}
