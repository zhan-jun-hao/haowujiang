package com.haowujiang.sanguosha.interfaces.vo.battle.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建对战请求对象
 */
@Data
public class CreateBattleReqVo {

    /**
     * 玩家选择的武将id
     */
    @NotNull
    private Long playerGeneralId;

    /**
     * 玩家选择对战的武将id
     */
    @NotNull
    private Long enemyGeneralId;
}
