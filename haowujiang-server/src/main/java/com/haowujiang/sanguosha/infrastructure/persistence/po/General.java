package com.haowujiang.sanguosha.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.haowujiang.sanguosha.domain.enums.GeneralUnlockSource;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 武将持久化对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("general")
public class General extends BaseEntity {

    /**
     * 武将唯一编码，例如：zhao-yun、zhang-fei。
     */
    private String code;

    /**
     * 武将名称。
     */
    private String name;

    /**
     * 武将称号。
     */
    private String title;

    /**
     * 所属阵营，例如：蜀、魏、吴、群。
     */
    private String camp;

    /**
     * 体力上限。
     */
    private Integer hp;

    /**
     * 稀有度或获取类型展示文案。
     */
    private String rarity;

    /**
     * 技能名称。
     */
    @TableField("skillName")
    private String skillName;

    /**
     * 技能摘要。
     */
    @TableField("skillSummary")
    private String skillSummary;

    /**
     * 获取来源：0 默认拥有，1 秒杀获取。
     */
    @TableField("unlockSource")
    private GeneralUnlockSource unlockSource;
}
