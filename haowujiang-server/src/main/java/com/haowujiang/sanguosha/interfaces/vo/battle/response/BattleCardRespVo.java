package com.haowujiang.sanguosha.interfaces.vo.battle.response;

import lombok.Data;

/**
 * 对战卡牌响应对象。
 */
@Data
public class BattleCardRespVo {

    /**
     * 卡牌 ID。
     */
    private String id;

    /**
     * 卡牌类型。
     */
    private String kind;

    /**
     * 卡牌名称。
     */
    private String name;

    /**
     * 花色。
     */
    private String suit;

    /**
     * 点数。
     */
    private String point;
}
