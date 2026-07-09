package com.haowujiang.sanguosha.application.vo.seckill.response;

import lombok.Data;

/**
 * 秒杀订单基础视图对象，对应 seckillOrder 表。
 */
@Data
public class SeckillOrderBasicVo {

    /**
     * 主键 ID。
     */
    private String id;

    /**
     * 用户 ID。
     */
    private Long userId;

    /**
     * 武将编码。
     */
    private String generalId;

    /**
     * 订单状态。
     */
    private String status;

    /**
     * 创建时间。
     */
    private String createdAt;
}
