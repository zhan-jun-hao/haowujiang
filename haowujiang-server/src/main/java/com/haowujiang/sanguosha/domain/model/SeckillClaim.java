package com.haowujiang.sanguosha.domain.model;

import com.haowujiang.sanguosha.infrastructure.persistence.po.SeckillOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 秒杀领取结果领域模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillClaim {

    /**
     * 领取状态
     */
    private Status status;

    /**
     * 秒杀订单
     */
    private SeckillOrder order;

    public static SeckillClaim success(SeckillOrder order) {
        return new SeckillClaim(Status.SUCCESS, order);
    }

    public static SeckillClaim duplicate() {
        return new SeckillClaim(Status.DUPLICATE, null);
    }

    public static SeckillClaim soldOut() {
        return new SeckillClaim(Status.SOLD_OUT, null);
    }

    /**
     * 秒杀领取状态枚举
     */
    public enum Status {

        /**
         * 领取成功
         */
        SUCCESS,

        /**
         * 重复领取
         */
        DUPLICATE,

        /**
         * 库存已抢完
         */
        SOLD_OUT
    }
}
