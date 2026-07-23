package com.haowujiang.sanguosha.interfaces.vo.battle.response;

import java.util.List;
import lombok.Data;

/**
 * 对战详情响应对象
 */
@Data
public class BattleRespVo {

    /**
     * 对战 ID
     */
    private String id;

    /**
     * 当前回合数
     */
    private Integer round;

    /**
     * 当前行动方
     */
    private String current;

    /**
     * 玩家角色信息
     */
    private BattleActorRespVo player;

    /**
     * 敌方角色信息
     */
    private BattleActorRespVo enemy;

    /**
     * 牌堆剩余数量
     */
    private Integer deckCount;

    /**
     * 弃牌堆数量
     */
    private Integer discardCount;

    /**
     * 对战日志
     */
    private List<BattleLogRespVo> logs;

    /**
     * AI 思考过程
     */
    private List<String> aiThoughts;

    /**
     * 等待玩家响应的状态。
     */
    private BattlePendingResponseRespVo pendingResponse;

    /**
     * 胜利方
     */
    private String winner;
}
