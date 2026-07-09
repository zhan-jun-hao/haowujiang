package com.haowujiang.sanguosha.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 玩家操作类型枚举。
 * <p>
 * 这是玩家 / AI 从外部发来的「指令」，与引擎内部的 {@link BattleTiming 时机窗口} 是两层不同的概念。
 * 一个 PlayerAction 的执行过程中，引擎会在多个 BattleTiming 点发布事件给技能系统。
 * </p>
 *
 * <pre>
 *     前端传来: PLAY_CARD + cardId
 *       → BattleCardDomainService.useSha()
 *         → publish(CARD_USED)       ← 时机窗口 1
 *         → resolveAttack()
 *           → publish(DAMAGE_BEFORE)  ← 时机窗口 2
 *           → publish(DAMAGE_AFTER)   ← 时机窗口 3
 *             → 奸雄技能触发
 * </pre>
 */
@Getter
@RequiredArgsConstructor
public enum PlayerAction {

    /**
     * 出牌 —— 使用手牌中的一张牌（杀/桃/酒等，引擎自动判断牌的用途）
     */
    PLAY_CARD("PLAY_CARD", "出牌"),

    /**
     * 结束回合 —— 不再出牌，切换到对方回合
     */
    END_TURN("END_TURN", "结束回合"),

    /**
     * 响应出牌 —— 被要求响应时选择出牌（例如被杀后出闪）
     */
    RESPOND_CARD("RESPOND_CARD", "响应出牌"),

    /**
     * 放弃响应 —— 被要求响应时选择不出牌（例如被杀后选择不出闪，硬吃伤害）
     */
    PASS_RESPONSE("PASS_RESPONSE", "放弃响应");

    /**
     * 对外序列化时使用的字符串值（前端 / AI 通过这个值交互）
     */
    @JsonValue
    private final String value;

    /**
     * 中文标签，便于日志和调试
     */
    private final String label;

    /**
     * 根据字符串值查找对应的枚举常量（大小写不敏感）。
     *
     * @param value 字符串值，如 "PLAY_CARD"、"end_turn"
     * @return 匹配的枚举常量，未匹配时返回 null
     */
    @JsonCreator
    public static PlayerAction fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (PlayerAction action : values()) {
            if (action.value.equalsIgnoreCase(value)) {
                return action;
            }
        }
        return null;
    }
}
