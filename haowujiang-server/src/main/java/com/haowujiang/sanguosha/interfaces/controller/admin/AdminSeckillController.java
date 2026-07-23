package com.haowujiang.sanguosha.interfaces.controller.admin;

import com.haowujiang.sanguosha.application.service.SeckillActivityApplicationService;
import com.haowujiang.sanguosha.infrastructure.common.PageResult;
import com.haowujiang.sanguosha.interfaces.vo.seckill.request.CreateSeckillActivityReqVo;
import com.haowujiang.sanguosha.interfaces.vo.seckill.request.SeckillActivityPageQueryReqVo;
import com.haowujiang.sanguosha.interfaces.vo.seckill.response.SeckillActivityRespVo;
import com.haowujiang.sanguosha.infrastructure.common.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * admin端-秒杀活动管理
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/seckill/activities")
public class AdminSeckillController {

    private final SeckillActivityApplicationService seckillActivityApplicationService;

    /**
     * 分页查询秒杀活动
     */
    @GetMapping
    public Result<PageResult<SeckillActivityRespVo>> pageQuery(@Validated SeckillActivityPageQueryReqVo query) {
        return Result.success(seckillActivityApplicationService.pageQuery(query));
    }

    /**
     * 查询活动详情
     */
    @GetMapping("/{activityCode}")
    public Result<SeckillActivityRespVo> getByActivityCode(@PathVariable("activityCode") String activityCode) {
        return Result.success(seckillActivityApplicationService.getByActivityCodeDirect(activityCode));
    }

    /**
     * 上架秒杀武将
     */
    @PostMapping
    public Result<SeckillActivityRespVo> createActivity(@Valid @RequestBody CreateSeckillActivityReqVo reqVo) {
        return Result.success(seckillActivityApplicationService.createActivity(reqVo));
    }

    /**
     * 下架秒杀武将
     */
    @PutMapping("/{activityCode}/offline")
    public Result<Void> offlineActivity(@PathVariable("activityCode") String activityCode) {
        seckillActivityApplicationService.offlineActivity(activityCode);
        return Result.success();
    }

    /**
     * 开启秒杀活动
     */
    @PutMapping("/{activityCode}/enable")
    public Result<Void> enableActivity(@PathVariable("activityCode") String activityCode) {
        seckillActivityApplicationService.enableActivity(activityCode);
        return Result.success();
    }

}
