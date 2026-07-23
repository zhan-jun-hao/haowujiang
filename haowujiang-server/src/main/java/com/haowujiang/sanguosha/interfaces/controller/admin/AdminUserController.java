package com.haowujiang.sanguosha.interfaces.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.haowujiang.sanguosha.application.service.GeneralApplicationService;
import com.haowujiang.sanguosha.application.service.UserApplicationService;
import com.haowujiang.sanguosha.infrastructure.common.PageResult;
import com.haowujiang.sanguosha.interfaces.vo.general.request.UserGeneralPageQueryReqVo;
import com.haowujiang.sanguosha.interfaces.vo.general.response.MyGeneralRespVo;
import com.haowujiang.sanguosha.interfaces.vo.user.request.AdminUserPageQueryReqVo;
import com.haowujiang.sanguosha.interfaces.vo.user.response.AdminUserBasicVo;
import com.haowujiang.sanguosha.infrastructure.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * admin端-用户管理
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final GeneralApplicationService generalApplicationService;
    private final UserApplicationService userApplicationService;

    /**
     * 分页查询User基础信息
     * @param query
     * @return
     */
    @GetMapping
    public Result<PageResult<AdminUserBasicVo>> getUserPageList(@Validated AdminUserPageQueryReqVo query) {
        log.info("分页查询, UserController -> /api/main/admin/users, query: {}", JSONObject.toJSONString(query));
        return Result.success(userApplicationService.getPageListUsers(query));
    }

    /**
     * 查询用户详情
     * @param userId
     * @return
     */
    @GetMapping("/{userId}")
    public Result<AdminUserBasicVo> getUserInfo(@PathVariable("userId") Long userId) {
        return Result.success(userApplicationService.getUserInfo(userId));
    }

    /**
     * 分页查询用户拥有的武将
     * @param userId
     * @param query
     * @return
     */
    @GetMapping("/{userId}/generals")
    public Result<PageResult<MyGeneralRespVo>> listUserGenerals(@PathVariable("userId") Long userId,
            @Validated UserGeneralPageQueryReqVo query) {
        return Result.success(generalApplicationService.listOwnedGenerals(userId, query));
    }

    /**
     * 发放武将
     * @param userId
     * @param generalId
     * @return
     */
    @PostMapping("/{userId}/generals/{generalId}")
    public Result<MyGeneralRespVo> grantGeneral(@PathVariable("userId") Long userId
            , @PathVariable("generalId") Long generalId) {
        return Result.success(generalApplicationService.grantGeneral(userId, generalId));
    }
}
