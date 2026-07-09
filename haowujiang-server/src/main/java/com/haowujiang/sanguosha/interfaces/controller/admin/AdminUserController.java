package com.haowujiang.sanguosha.interfaces.controller.admin;

import com.haowujiang.sanguosha.application.service.GeneralApplicationService;
import com.haowujiang.sanguosha.application.service.UserApplicationService;
import com.haowujiang.sanguosha.application.vo.general.response.MyGeneralRespVo;
import com.haowujiang.sanguosha.application.vo.user.response.UserBasicVo;
import com.haowujiang.sanguosha.infrastructure.common.Result;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final GeneralApplicationService generalApplicationService;
    private final UserApplicationService userApplicationService;

    @GetMapping
    public Result<List<UserBasicVo>> listUsers() {
        return Result.success(userApplicationService.listUsers());
    }

    @GetMapping("/{userId}")
    public Result<UserBasicVo> getUserInfo(@PathVariable("userId") Long userId) {
        return Result.success(userApplicationService.getUserInfo(userId));
    }

    @GetMapping("/{userId}/generals")
    public Result<List<MyGeneralRespVo>> listUserGenerals(@PathVariable("userId") Long userId) {
        return Result.success(generalApplicationService.listOwnedGenerals(userId));
    }

    @PostMapping("/{userId}/generals/{generalId}")
    public Result<MyGeneralRespVo> grantGeneral(@PathVariable("userId") Long userId
            , @PathVariable("generalId") Long generalId) {
        return Result.success(generalApplicationService.grantGeneral(userId, generalId));
    }
}
