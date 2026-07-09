package com.haowujiang.sanguosha.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 武将秒杀库存持久化对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("seckillStock")
public class SeckillStock extends BaseEntity {

    /**
     * 武将编码。
     */
    @TableField("generalCode")
    private String generalCode;

    /**
     * 总库存。
     */
    @TableField("totalStock")
    private Integer totalStock;

    /**
     * 可用库存。
     */
    @TableField("availableStock")
    private Integer availableStock;
}
