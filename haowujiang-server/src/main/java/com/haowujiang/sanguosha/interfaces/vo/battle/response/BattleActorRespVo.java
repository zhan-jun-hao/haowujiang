package com.haowujiang.sanguosha.interfaces.vo.battle.response;

import java.util.List;
import lombok.Data;

/**
 * 对战角色响应对象。
 */
@Data
public class BattleActorRespVo {

    /**
     * 行动方：player 玩家，enemy 敌方。
     */
    private String side;

    /**
     * 武将编码。
     */
    private String generalId;

    /**
     * 武将名称。
     */
    private String name;

    /**
     * 技能名称
     */
    private String skillName;

    /**
     * 当前体力。
     */
    private Integer hp;

    /**
     * 体力上限。
     */
    private Integer maxHp;

    /**
     * 手牌列表。
     */
    private List<BattleCardRespVo> hand;

    /**
     * 手牌数量。
     */
    private Integer handCount;

    /**
     * 是否已使用酒。
     */
    private Boolean drunk;

    /**
     * 本回合已使用杀的次数。
     */
    private Integer shaUsed;

    /**
     * 是否已战败。
     */
    private Boolean defeated;
}
