package com.haowujiang.sanguosha.domain.model;

import com.haowujiang.sanguosha.infrastructure.persistence.po.SeckillOrder;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 秒杀状态领域模型
 */
@Data
@NoArgsConstructor
public class SeckillState {

    /**
     * 目标武将编码
     */
    private String targetGeneralId;

    /**
     * 剩余库存
     */
    private int stock;

    /**
     * 总库存
     */
    private int total;

    /**
     * 已领取用户 ID 列表
     */
    private List<Long> claimedUserIds;

    /**
     * 秒杀订单列表
     */
    private List<SeckillOrder> orders;

    /**
     * 秒杀事件列表
     */
    private List<SeckillEvent> events;

    public SeckillState(String targetGeneralId, int stock, int total, List<Long> claimedUserIds,
                        List<SeckillOrder> orders, List<SeckillEvent> events) {
        this.targetGeneralId = targetGeneralId;
        this.stock = stock;
        this.total = total;
        this.claimedUserIds = claimedUserIds == null ? new ArrayList<>() : new ArrayList<>(claimedUserIds);
        this.orders = orders == null ? new ArrayList<>() : new ArrayList<>(orders);
        this.events = events == null ? new ArrayList<>() : new ArrayList<>(events);
    }
}
