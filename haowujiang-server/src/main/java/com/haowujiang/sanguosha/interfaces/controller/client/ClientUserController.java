package com.haowujiang.sanguosha.interfaces.controller.client;

import com.haowujiang.sanguosha.application.service.GeneralApplicationService;
import com.haowujiang.sanguosha.application.vo.general.response.MyGeneralRespVo;
import com.haowujiang.sanguosha.infrastructure.common.Result;
import com.haowujiang.sanguosha.infrastructure.security.context.UserContextHolder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/client/users/me")
@RequiredArgsConstructor
public class ClientUserController {

    private final GeneralApplicationService generalApplicationService;

    @GetMapping("/generals")
    public Result<List<MyGeneralRespVo>> listMyGenerals() {
        return Result.success(generalApplicationService.listOwnedGenerals(UserContextHolder.getRequiredUserId()));
    }
}
