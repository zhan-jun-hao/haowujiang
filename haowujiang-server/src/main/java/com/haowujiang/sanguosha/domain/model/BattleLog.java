package com.haowujiang.sanguosha.domain.model;

import com.haowujiang.sanguosha.domain.enums.BattleLogType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对战日志领域模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BattleLog {

    /**
     * 日志 ID
     */
    private String id;

    /**
     * 日志类型
     */
    private BattleLogType type;

    /**
     * 日志内容
     */
    private String text;
}
