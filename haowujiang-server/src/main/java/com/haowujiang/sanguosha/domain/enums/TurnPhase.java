package com.haowujiang.sanguosha.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 回合阶段枚举 —— 显式状态机。
 * <p>
 * 之前回合的状态转换藏在方法调用链里（endPlayerTurn → runAiTurn → startPlayerTurnIfNeed），
 * 现在通过 phase 字段显式声明当前处于哪个阶段，Service 层变成纯粹的 switch 分发。
 * </p>
 *
 * <pre>
 * 状态流转图:
 *
 *   PLAYER_ACTION ──(END_TURN)──→ ENEMY_ACTION
 *       │                              │
 *       │ (出杀后被闪，或 AI 出杀)       │ (AI 出杀 → 等玩家出闪)
 *       ↓                              ↓
 *   PLAYER_RESPONDING ←────────── ENEMY 攻击玩家
 *       │
 *       │ (出闪 / 不出闪 → 伤害结算)
 *       ↓
 *   ┌─ ENEMY_ACTION（继续 AI 回合）  或  TURN_END（如果 AI 回合已结束）
 *   │
 *   TURN_END ──(新回合开始)──→ PLAYER_ACTION
 *       │
 *       │ (一方 HP ≤ 0)
 *       ↓
 *   FINISHED
 * </pre>
 */
@Getter
@RequiredArgsConstructor
public enum TurnPhase {

    /**
     * 玩家出牌阶段 —— 等待玩家选择出牌或结束回合
     */
    PLAYER_ACTION(0, "玩家出牌阶段"),

    /**
     * 玩家响应阶段 —— 被对方出杀后，等待玩家选择出闪或放弃
     */
    PLAYER_RESPONDING(1, "玩家响应阶段"),

    /**
     * AI 出牌阶段 —— AI 自动决策出牌
     */
    ENEMY_ACTION(2, "AI 出牌阶段"),

    /**
     * AI 响应阶段 —— 被玩家出杀后，等待 AI 选择出闪或放弃（预留，当前未使用）
     */
    ENEMY_RESPONDING(3, "AI 响应阶段"),

    /**
     * 回合结束结算阶段 —— 触发回合结束相关技能和效果
     */
    TURN_END(4, "回合结束结算"),

    /**
     * 对战已结束 —— 一方被击败
     */
    FINISHED(5, "对战已结束");

    private final Integer code;

    private final String label;
}
