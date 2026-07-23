package com.haowujiang.sanguosha.interfaces.controller.client;

import com.haowujiang.sanguosha.application.service.SeckillActivityApplicationService;
import com.haowujiang.sanguosha.application.service.SeckillApplicationService;
import com.haowujiang.sanguosha.interfaces.vo.seckill.response.SeckillActivityRespVo;
import com.haowujiang.sanguosha.interfaces.vo.seckill.response.SeckillResultRespVo;
import com.haowujiang.sanguosha.interfaces.vo.seckill.response.SeckillStateRespVo;
import com.haowujiang.sanguosha.infrastructure.common.Result;
import com.haowujiang.sanguosha.infrastructure.security.context.UserContextHolder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * client端-秒杀服务
 */
@RestController
@RequestMapping("/api/client/seckill")
@RequiredArgsConstructor
public class ClientSeckillController {

    private final SeckillApplicationService seckillApplicationService;
    private final SeckillActivityApplicationService seckillActivityApplicationService;

    /**
     * 查询活动详情（带缓存，应对高并发读）
     */
    @GetMapping("/activities/{activityCode}")
    public Result<SeckillActivityRespVo> getByActivityCode(@PathVariable("activityCode") String activityCode) {
        return Result.success(seckillActivityApplicationService.getByActivityCode(activityCode));
    }

    /**
     * 查询所有进行中的秒杀活动
     */
    @GetMapping("/activities")
    public Result<List<SeckillActivityRespVo>> listActiveActivities() {
        return Result.success(seckillApplicationService.listActiveActivities());
    }

    /**
     * 显示武将秒杀库存信息
     */
    @GetMapping("/generals/{generalCode}/state")
    public Result<SeckillStateRespVo> getState(@PathVariable("generalCode") String generalCode) {
        return Result.success(seckillApplicationService.getState(generalCode));
    }

    /**
     * 具体秒杀
     */
    @PostMapping("/generals/{generalCode}")
    public Result<SeckillResultRespVo> claim(@PathVariable("generalCode") String generalCode) {
        return Result.success(seckillApplicationService.claim(UserContextHolder.getRequiredUserId(), generalCode));
    }
}
