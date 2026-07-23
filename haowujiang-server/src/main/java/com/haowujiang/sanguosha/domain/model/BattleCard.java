package com.haowujiang.sanguosha.domain.model;

import com.haowujiang.sanguosha.infrastructure.enums.CardKind;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对战卡牌领域模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BattleCard {

    /**
     * 卡牌ID
     */
    private String id;

    /**
     * 卡牌类型
     */
    private CardKind kind;

    /**
     * 花色
     */
    private String suit;

    /**
     * 点数
     */
    private String point;

    /**
     * 卡牌名称
     */
    public String getName() {
        return kind.getLabel();
    }

}
