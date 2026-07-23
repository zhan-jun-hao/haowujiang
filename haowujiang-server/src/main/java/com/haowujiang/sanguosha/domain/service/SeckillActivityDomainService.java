package com.haowujiang.sanguosha.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haowujiang.sanguosha.infrastructure.persistence.po.SeckillActivity;
import com.haowujiang.sanguosha.interfaces.vo.seckill.request.SeckillActivityPageQueryReqVo;
import java.util.List;

/**
 * 秒杀活动领域服务接口
 */
public interface SeckillActivityDomainService {

    /**
     * 分页查询秒杀活动
     */
    IPage<SeckillActivity> pageQuery(SeckillActivityPageQueryReqVo query);

    /**
     * 根据活动编码查询
     */
    SeckillActivity getByActivityCode(String activityCode);

    /**
     * 根据武将编码查询正在进行的活动
     */
    SeckillActivity getActiveByGeneralCode(String generalCode);

    /**
     * 创建秒杀活动
     */
    SeckillActivity createActivity(SeckillActivity activity);

    /**
     * 更新活动状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 查询所有有效活动（待开始 + 进行中）
     */
    List<SeckillActivity> listActive();

    /**
     * 扣减库存（原子操作）
     */
    boolean deductStock(String generalCode);
}
