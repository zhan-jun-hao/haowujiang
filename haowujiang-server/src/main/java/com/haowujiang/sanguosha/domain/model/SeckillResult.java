package com.haowujiang.sanguosha.domain.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 秒杀业务结果领域模型
 */
@Data
@NoArgsConstructor
public class SeckillResult {

    /**
     * 是否成功
     */
    private boolean ok;

    /**
     * 结果标题
     */
    private String title;

    /**
     * 结果消息
     */
    private String message;

    /**
     * 处理步骤列表
     */
    private List<String> steps;

    /**
     * 秒杀状态快照
     */
    private SeckillState state;

    public SeckillResult(boolean ok, String title, String message, List<String> steps, SeckillState state) {
        this.ok = ok;
        this.title = title;
        this.message = message;
        this.steps = steps == null ? new ArrayList<>() : new ArrayList<>(steps);
        this.state = state;
    }
}
