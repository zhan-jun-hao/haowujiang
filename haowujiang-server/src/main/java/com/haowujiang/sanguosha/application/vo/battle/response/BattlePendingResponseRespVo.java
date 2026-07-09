package com.haowujiang.sanguosha.application.vo.battle.response;

import lombok.Data;

/**
 * 等待玩家响应的状态。
 */
@Data
public class BattlePendingResponseRespVo {

    private String type;

    private String attackerSide;

    private String defenderSide;

    private String attackerName;

    private String defenderName;

    private String attackCardId;

    private String attackCardName;

    private String attackCardSuit;

    private String attackCardPoint;

    private Integer damage;
}
