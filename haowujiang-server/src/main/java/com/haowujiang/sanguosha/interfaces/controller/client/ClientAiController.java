package com.haowujiang.sanguosha.interfaces.controller.client;

import com.haowujiang.sanguosha.application.service.ClientAiApplicationService;
import com.haowujiang.sanguosha.interfaces.vo.ai.request.ClientAiChatReqVo;
import com.haowujiang.sanguosha.interfaces.vo.ai.response.ClientAiChatRespVo;
import com.haowujiang.sanguosha.infrastructure.common.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客户端 AI 控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client/ai")
public class ClientAiController {

    private final ClientAiApplicationService clientAiApplicationService;

    @PostMapping("/chat")
    public Result<ClientAiChatRespVo> chat(@Valid @RequestBody ClientAiChatReqVo reqVo) {
        return Result.success(clientAiApplicationService.chat(reqVo));
    }
}
