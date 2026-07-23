package com.haowujiang.sanguosha.interfaces.vo.seckill.response;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 秒杀活动响应对象
 */
@Data
public class SeckillActivityRespVo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 活动编码
     */
    private String activityCode;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 武将编码
     */
    private String generalCode;

    /**
     * 武将名
     */
    private String generalName;

    /**
     * 秒杀总库存
     */
    private Integer stock;

    /**
     * 剩余库存
     */
    private Integer availableStock;

    /**
     * 每人限购数量
     */
    private Integer limitPerUser;

    /**
     * 秒杀开始时间
     */
    private LocalDateTime startTime;

    /**
     * 秒杀结束时间
     */
    private LocalDateTime endTime;

    /**
     * 状态：0-待开始 1-进行中 2-已结束 3-已下架
     */
    private Integer status;

    /**
     * 状态文本
     */
    private String statusText;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}
