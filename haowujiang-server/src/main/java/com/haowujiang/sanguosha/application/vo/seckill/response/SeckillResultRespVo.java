package com.haowujiang.sanguosha.application.vo.seckill.response;

import java.util.List;
import lombok.Data;

/**
 * 秒杀结果响应对象
 */
@Data
public class SeckillResultRespVo {

    /**
     * 是否成功
     */
    private Boolean ok;

    /**
     * 响应标题
     */
    private String title;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 秒杀处理步骤
     */
    private List<String> steps;

    /**
     * 秒杀状态
     */
    private SeckillStateRespVo state;
}
