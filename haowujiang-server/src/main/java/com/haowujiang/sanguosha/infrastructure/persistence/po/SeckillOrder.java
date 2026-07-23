package com.haowujiang.sanguosha.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.haowujiang.sanguosha.infrastructure.enums.SeckillOrderStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 武将秒杀订单持久化对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("seckillOrder")
public class SeckillOrder extends BaseEntity {

    /**
     * 秒杀订单号。
     */
    @TableField("orderNo")
    private String orderNo;

    /**
     * 用户 ID。
     */
    @TableField("userId")
    private Long userId;

    /**
     * 武将编码。
     */
    @TableField("generalCode")
    private String generalCode;

    /**
     * 订单状态：0 已创建，1 队列中，2 失败。
     */
    private SeckillOrderStatus status;
}
