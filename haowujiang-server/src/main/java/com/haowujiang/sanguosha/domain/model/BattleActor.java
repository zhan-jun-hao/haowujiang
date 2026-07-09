package com.haowujiang.sanguosha.domain.model;

import com.haowujiang.sanguosha.domain.enums.Side;
import com.haowujiang.sanguosha.infrastructure.persistence.po.General;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对战角色领域模型 —— 战场上的一个武将。
 * <p>
 * 包含武将的基本信息（名称、体力）、战斗状态（手牌、酒状态、出杀次数）
 * 以及技能名称（逗号分隔，如 "龙胆" 或 "咆哮,奸雄"）。
 * </p>
 */
@Data
@NoArgsConstructor
public class BattleActor {

    /**
     * 阵营（玩家 / 敌方）
     */
    private Side side;

    /**
     * 武将编码（如 "zhao-yun"）
     */
    private String generalId;

    /**
     * 武将名称（如 "赵云"）
     */
    private String name;

    /**
     * 技能名称，逗号分隔（如 "龙胆" 或 "咆哮,奸雄"）
     */
    private String skillName;

    /**
     * 体力上限
     */
    private int maxHp;

    /**
     * 当前体力
     */
    private int hp;

    /**
     * 当前手牌列表
     */
    private List<BattleCard> hand = new ArrayList<>();

    /**
     * 是否处于酒状态（下一张杀的伤害 +1）
     */
    private boolean drunk;

    /**
     * 本回合已使用杀的次数
     */
    private int shaUsed;

    /**
     * 是否已被击败（体力归零）
     */
    private boolean defeated;

    /**
     * 从数据库 General PO 构造对战角色。
     *
     * @param side    阵营
     * @param general 武将数据
     */
    public BattleActor(Side side, General general) {
        this.side = side;
        this.generalId = general.getCode();
        this.name = general.getName();
        this.skillName = general.getSkillName();
        this.hp = general.getHp();
        this.maxHp = general.getHp();
    }
}
