package com.haowujiang.sanguosha.interfaces.controller.client;

import com.haowujiang.sanguosha.application.service.BattleApplicationService;
import com.haowujiang.sanguosha.application.vo.battle.request.BattleActionReqVo;
import com.haowujiang.sanguosha.application.vo.battle.request.CreateBattleReqVo;
import com.haowujiang.sanguosha.application.vo.battle.response.BattleRespVo;
import com.haowujiang.sanguosha.infrastructure.common.Result;
import com.haowujiang.sanguosha.infrastructure.security.context.UserContextHolder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客户端对战接口。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client/battles")
public class ClientBattleController {

    private final BattleApplicationService battleApplicationService;

    /**
     * 创建对局
     */
    @PostMapping
    public Result<BattleRespVo> createBattle(@Valid @RequestBody CreateBattleReqVo request) {
        return Result.success(battleApplicationService.createBattle(
                UserContextHolder.getRequiredUserId(),
                request.getPlayerGeneralId(),
                request.getEnemyGeneralId()
        ));
    }

    /**
     * 查看对局
     */
    @GetMapping("/{battleId}")
    public Result<BattleRespVo> getBattle(@PathVariable String battleId) {
        return Result.success(battleApplicationService.getBattle(battleId));
    }

    /**
     * 玩家行动 —— 出牌 / 结束回合 / 响应出牌
     *
     * @param battleId 对战编号
     * @param request  行动请求（action + cardId）
     * @return 最新对战状态
     */
    @PostMapping("/{battleId}/actions")
    public Result<BattleRespVo> act(@PathVariable("battleId") String battleId,
                                     @RequestBody BattleActionReqVo request) {
        return Result.success(battleApplicationService.act(battleId, request.getAction(), request.getCardId()));
    }

    /**
     * 触发 AI 行动
     */
    @PostMapping("/{battleId}/ai-action")
    public Result<BattleRespVo> runAiAction(@PathVariable String battleId) {
        return Result.success(battleApplicationService.runAiAction(battleId));
    }
}
