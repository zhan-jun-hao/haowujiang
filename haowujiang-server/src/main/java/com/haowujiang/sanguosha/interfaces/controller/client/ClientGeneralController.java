package com.haowujiang.sanguosha.interfaces.controller.client;

import com.haowujiang.sanguosha.application.service.GeneralApplicationService;
import com.haowujiang.sanguosha.infrastructure.common.PageResult;
import com.haowujiang.sanguosha.interfaces.vo.general.request.GeneralPageQueryReqVo;
import com.haowujiang.sanguosha.interfaces.vo.general.response.GeneralBasicVo;
import com.haowujiang.sanguosha.interfaces.vo.general.response.GeneralDetailVo;
import com.haowujiang.sanguosha.infrastructure.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * client端-武将服务
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client/generals")
public class ClientGeneralController {

    private final GeneralApplicationService generalApplicationService;

    /**
     * 分页查询武将
     * @param query
     * @return
     */
    @GetMapping
    public Result<PageResult<GeneralBasicVo>> listGenerals(@Validated GeneralPageQueryReqVo query) {
        return Result.success(generalApplicationService.listGenerals(query));
    }

    /**
     * 查询武将详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<GeneralDetailVo> getGeneralInfo(@PathVariable("id") Long id) {
        return Result.success(generalApplicationService.getGeneralInfo(id));
    }
}
