package com.haowujiang.sanguosha.application.service;

import com.haowujiang.sanguosha.infrastructure.common.PageResult;
import com.haowujiang.sanguosha.interfaces.vo.seckill.request.CreateSeckillActivityReqVo;
import com.haowujiang.sanguosha.interfaces.vo.seckill.request.SeckillActivityPageQueryReqVo;
import com.haowujiang.sanguosha.interfaces.vo.seckill.response.SeckillActivityRespVo;

/**
 * 秒杀活动应用服务接口
 */
public interface SeckillActivityApplicationService {

    /**
     * 分页查询秒杀活动
     */
    PageResult<SeckillActivityRespVo> pageQuery(SeckillActivityPageQueryReqVo query);

    /**
     * 查询活动详情（带缓存，供客户端高并发读）
     */
    SeckillActivityRespVo getByActivityCode(String activityCode);

    /**
     * 查询活动详情
     */
    SeckillActivityRespVo getByActivityCodeDirect(String activityCode);

    /**
     * 上架秒杀武将
     */
    SeckillActivityRespVo createActivity(CreateSeckillActivityReqVo reqVo);

    /**
     * 下架秒杀武将
     */
    void offlineActivity(String activityCode);

    /**
     * 开启秒杀活动
     */
    void enableActivity(String activityCode);
}
