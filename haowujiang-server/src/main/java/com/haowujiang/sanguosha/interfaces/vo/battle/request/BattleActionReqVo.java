package com.haowujiang.sanguosha.interfaces.vo.battle.request;

import com.haowujiang.sanguosha.infrastructure.enums.PlayerAction;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 对战行动请求对象。
 * <p>
 * action 字段直接接收 {@link PlayerAction} 枚举，Jackson 通过
 * {@link PlayerAction#fromValue(String)} 上标注的 {@code @JsonCreator} 自动完成反序列化，
 * 无需 Controller 层手动转换。
 * </p>
 */
@Data
public class BattleActionReqVo {

    /**
     * 行动类型：PLAY_CARD 出牌，END_TURN 结束回合，RESPOND_CARD 响应出牌，PASS_RESPONSE 放弃响应。
     */
    @NotNull
    private PlayerAction action;

    /**
     * 需要使用的手牌 ID（出牌 / 响应出牌时必填）。
     */
    private String cardId;
}
