package com.haowujiang.sanguosha.interfaces.vo.seckill.response;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 秒杀状态响应对象（客户端用，仅返回展示所需字段）
 */
@Data
public class SeckillStateRespVo {

    /**
     * 武将编码
     */
    private String generalCode;

    /**
     * 活动编码
     */
    private String activityCode;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 武将名
     */
    private String generalName;

    /**
     * 剩余库存
     */
    private Integer stock;

    /**
     * 总库存
     */
    private Integer total;

    /**
     * 活动状态：0-待开始 1-进行中 2-已结束 3-已下架
     */
    private Integer status;

    /**
     * 每人限购数量
     */
    private Integer limitPerUser;

    /**
     * 活动开始时间
     */
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime endTime;
}
