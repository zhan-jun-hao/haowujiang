package com.haowujiang.sanguosha.interfaces.controller.client;

import com.haowujiang.sanguosha.application.service.GeneralApplicationService;
import com.haowujiang.sanguosha.application.vo.general.response.GeneralBasicVo;
import com.haowujiang.sanguosha.application.vo.general.response.GeneralDetailVo;
import com.haowujiang.sanguosha.infrastructure.common.Result;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client/generals")
public class ClientGeneralController {

    private final GeneralApplicationService generalApplicationService;

    @GetMapping
    public Result<List<GeneralBasicVo>> listGenerals() {
        return Result.success(generalApplicationService.listGenerals());
    }

    @GetMapping("/{id}")
    public Result<GeneralDetailVo> getGeneralInfo(@PathVariable("id") Long id) {
        return Result.success(generalApplicationService.getGeneralInfo(id));
    }
}
