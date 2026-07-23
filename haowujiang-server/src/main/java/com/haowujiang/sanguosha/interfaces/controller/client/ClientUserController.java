package com.haowujiang.sanguosha.interfaces.controller.client;

import com.haowujiang.sanguosha.application.service.GeneralApplicationService;
import com.haowujiang.sanguosha.infrastructure.common.PageResult;
import com.haowujiang.sanguosha.interfaces.vo.general.request.UserGeneralPageQueryReqVo;
import com.haowujiang.sanguosha.interfaces.vo.general.response.MyGeneralRespVo;
import com.haowujiang.sanguosha.infrastructure.common.Result;
import com.haowujiang.sanguosha.infrastructure.security.context.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * client端-用户详情
 */
@RestController
@RequestMapping("/api/client/users/me")
@RequiredArgsConstructor
public class ClientUserController {

    private final GeneralApplicationService generalApplicationService;

    /**
     * 分页查询拥有的武将
     * @param query
     * @return
     */
    @GetMapping("/generals")
    public Result<PageResult<MyGeneralRespVo>> listMyGenerals(@Validated UserGeneralPageQueryReqVo query) {
        return Result.success(generalApplicationService.listOwnedGenerals(UserContextHolder.getRequiredUserId(), query));
    }
}
