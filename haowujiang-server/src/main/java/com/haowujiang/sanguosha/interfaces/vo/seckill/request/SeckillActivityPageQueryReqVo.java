package com.haowujiang.sanguosha.interfaces.vo.seckill.request;

import com.haowujiang.sanguosha.infrastructure.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 秒杀活动分页查询请求对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillActivityPageQueryReqVo extends PageQuery {

    /**
     * 活动状态：0-待开始 1-进行中 2-已结束 3-已下架，不传则查全部
     */
    private Integer status;

    /**
     * 武将编码
     */
    private String generalCode;
}
