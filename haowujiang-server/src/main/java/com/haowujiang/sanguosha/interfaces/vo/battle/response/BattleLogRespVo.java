package com.haowujiang.sanguosha.interfaces.vo.battle.response;

import lombok.Data;

/**
 * 对战日志响应对象。
 */
@Data
public class BattleLogRespVo {

    /**
     * 日志 ID。
     */
    private String id;

    /**
     * 日志类型。
     */
    private String type;

    /**
     * 日志内容。
     */
    private String text;
}
