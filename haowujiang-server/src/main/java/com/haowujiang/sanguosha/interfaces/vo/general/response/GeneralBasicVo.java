package com.haowujiang.sanguosha.interfaces.vo.general.response;

import com.haowujiang.sanguosha.infrastructure.enums.GeneralUnlockSource;
import lombok.Data;

/**
 * 武将基础视图对象，对应 general 表。
 */
@Data
public class GeneralBasicVo {

    /**
     * 武将 ID。
     */
    private Long id;

    /**
     * 武将唯一编码。
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
     * 所属阵营。
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
    private String skillName;

    /**
     * 技能摘要。
     */
    private String skillSummary;

    /**
     * 获取来源。
     */
    private GeneralUnlockSource unlockSource;
}
