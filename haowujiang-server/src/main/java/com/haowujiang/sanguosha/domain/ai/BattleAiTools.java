package com.haowujiang.sanguosha.domain.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haowujiang.sanguosha.domain.enums.CardKind;
import com.haowujiang.sanguosha.domain.enums.PlayerAction;
import com.haowujiang.sanguosha.domain.model.BattleActor;
import com.haowujiang.sanguosha.domain.model.BattleCard;
import com.haowujiang.sanguosha.domain.model.BattleState;
import com.haowujiang.sanguosha.domain.service.BattleCardDomainService;
import dev.langchain4j.agent.tool.Tool;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * AI 对战工具集 —— 暴露给 LangChain4j AI Agent 的 Tool 函数。
 * <p>
 * AI 通过这些工具查看自身状态、手牌和合法行动，然后返回 JSON 决策。
 * Tool 函数返回的 action 值使用 {@link PlayerAction#getValue()} 的字符串形式。
 * </p>
 */
@RequiredArgsConstructor
public class BattleAiTools {

    private final BattleState state;
    private final BattleCardDomainService battleCardDomainService;
    private final ObjectMapper objectMapper;

    @Tool("查看 AI 玩家当前状态")
    public String currentState() {
        BattleActor enemy = state.getEnemy();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("round", state.getRound());
        data.put("aiName", enemy.getName());
        data.put("skillName", enemy.getSkillName());
        data.put("hp", enemy.getHp());
        data.put("maxHp", enemy.getMaxHp());
        data.put("drunk", enemy.isDrunk());
        data.put("shaUsed", enemy.getShaUsed());
        data.put("playerHp", state.getPlayer().getHp());
        data.put("playerMaxHp", state.getPlayer().getMaxHp());
        return toJson(data);
    }

    @Tool("查看 AI 玩家手牌")
    public String handCards() {
        List<Map<String, Object>> cards = state.getEnemy().getHand().stream()
                .map(card -> {
                    Map<String, Object> data = new LinkedHashMap<>();
                    data.put("id", card.getId());
                    data.put("kind", card.getKind().name());
                    data.put("name", card.getName());
                    data.put("suit", card.getSuit());
                    data.put("point", card.getPoint());
                    return data;
                })
                .collect(Collectors.toCollection(ArrayList::new));
        return toJson(cards);
    }

    @Tool("查看 AI 玩家当前可以选择的合法动作")
    public String legalActions() {
        List<Map<String, Object>> actions = new ArrayList<>();
        for (BattleCard card : state.getEnemy().getHand()) {
            String reason = playableReason(card);
            if (StringUtils.hasText(reason)) {
                Map<String, Object> action = new LinkedHashMap<>();
                action.put("action", PlayerAction.PLAY_CARD.getValue());
                action.put("cardId", card.getId());
                action.put("cardName", card.getName());
                action.put("reason", reason);
                actions.add(action);
            }
        }
        Map<String, Object> endTurn = new LinkedHashMap<>();
        endTurn.put("action", PlayerAction.END_TURN.getValue());
        endTurn.put("cardId", null);
        endTurn.put("reason", "不再出牌，结束回合");
        actions.add(endTurn);
        return toJson(actions);
    }

    private String playableReason(BattleCard card) {
        if (battleCardDomainService.canUseAs(state, state.getEnemy(), card, CardKind.SHA)
                && battleCardDomainService.canUseSha(state, state.getEnemy(), card)) {
            return "可以当杀使用";
        }
        if (card.getKind() == CardKind.TAO && state.getEnemy().getHp() < state.getEnemy().getMaxHp()) {
            return "可以回复体力";
        }
        if (card.getKind() == CardKind.JIU && !state.getEnemy().isDrunk()
                && battleCardDomainService.findAttackCard(state, state.getEnemy()) != null) {
            return "可以提升下一张杀的伤害";
        }
        return null;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            return String.valueOf(value);
        }
    }
}
