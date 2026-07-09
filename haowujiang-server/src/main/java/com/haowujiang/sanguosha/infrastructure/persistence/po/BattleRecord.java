package com.haowujiang.sanguosha.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.haowujiang.sanguosha.domain.enums.BattleStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 对战记录持久化对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("battleRecord")
public class BattleRecord extends BaseEntity {

    /**
     * 对战编号
     */
    @TableField("battleNo")
    private String battleNo;

    /**
     * 用户 ID
     */
    @TableField("userId")
    private Long userId;

    /**
     * 玩家武将编码
     */
    @TableField("playerGeneralCode")
    private String playerGeneralCode;

    /**
     * 敌方武将编码
     */
    @TableField("enemyGeneralCode")
    private String enemyGeneralCode;

    /**
     * 当前回合数
     */
    private Integer round;

    /**
     * 当前行动方
     */
    @TableField("currentSide")
    private String currentSide;

    /**
     * 玩家当前体力
     */
    @TableField("playerHp")
    private Integer playerHp;

    /**
     * 敌方当前体力
     */
    @TableField("enemyHp")
    private Integer enemyHp;

    /**
     * 胜利方
     */
    private String winner;

    /**
     * 对战快照 JSON
     */
    @TableField("stateJson")
    private String stateJson;

    /**
     * 对战状态
     */
    private BattleStatus status;
}
