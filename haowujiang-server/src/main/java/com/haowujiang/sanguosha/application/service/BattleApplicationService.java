package com.haowujiang.sanguosha.application.service;

import com.haowujiang.sanguosha.interfaces.vo.battle.response.BattleRespVo;
import com.haowujiang.sanguosha.infrastructure.enums.PlayerAction;

/**
 * 对战应用服务接口。
 */
public interface BattleApplicationService {

    /**
     * 创建对局
     *
     * @param userId          用户 ID
     * @param playerGeneralId 玩家武将 ID
     * @param enemyGeneralId  敌方武将 ID
     * @return 对战响应
     */
    BattleRespVo createBattle(Long userId, Long playerGeneralId, Long enemyGeneralId);

    /**
     * 查看对局
     *
     * @param battleId 对战编号
     * @return 对战响应
     */
    BattleRespVo getBattle(String battleId);

    /**
     * 玩家行动（出牌 / 结束回合 / 响应出牌等）。
     *
     * @param battleId 对战编号
     * @param action   玩家操作类型
     * @param cardId   操作关联的手牌 ID
     * @return 更新后的对战响应
     */
    BattleRespVo act(String battleId, PlayerAction action, String cardId);

    /**
     * 触发 AI 行动
     *
     * @param battleId 对战编号
     * @return 更新后的对战响应
     */
    BattleRespVo runAiAction(String battleId);
}
