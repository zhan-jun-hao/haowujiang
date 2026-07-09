package com.haowujiang.sanguosha.interfaces.controller.client;

import com.haowujiang.sanguosha.application.service.SeckillApplicationService;
import com.haowujiang.sanguosha.application.vo.seckill.response.SeckillResultRespVo;
import com.haowujiang.sanguosha.application.vo.seckill.response.SeckillStateRespVo;
import com.haowujiang.sanguosha.infrastructure.common.Result;
import com.haowujiang.sanguosha.infrastructure.security.context.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/client/seckill/generals/{generalCode}")
@RequiredArgsConstructor
public class ClientSeckillController {

    private final SeckillApplicationService seckillApplicationService;

    @GetMapping("/state")
    public Result<SeckillStateRespVo> getState(@PathVariable("generalCode") String generalCode) {
        return Result.success(seckillApplicationService.getState(generalCode));
    }

    @PostMapping
    public Result<SeckillResultRespVo> claim(@PathVariable("generalCode") String generalCode) {
        return Result.success(seckillApplicationService.claim(UserContextHolder.getRequiredUserId(), generalCode));
    }
}
