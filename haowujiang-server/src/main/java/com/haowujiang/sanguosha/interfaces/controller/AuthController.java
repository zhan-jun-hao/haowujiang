package com.haowujiang.sanguosha.interfaces.controller;

import com.haowujiang.sanguosha.application.service.AuthApplicationService;
import com.haowujiang.sanguosha.interfaces.vo.auth.request.UserLoginReqVo;
import com.haowujiang.sanguosha.interfaces.vo.auth.response.UserLoginRespVo;
import com.haowujiang.sanguosha.infrastructure.common.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthApplicationService authApplicationService;

    @PostMapping("/login")
    public Result<UserLoginRespVo> login(@Valid @RequestBody UserLoginReqVo reqVo) {
        return Result.success(authApplicationService.login(reqVo));
    }
}
