package com.haowujiang.sanguosha.domain.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haowujiang.sanguosha.domain.ai.BattleAiPlayer;
import com.haowujiang.sanguosha.domain.ai.BattleAiTools;
import com.haowujiang.sanguosha.domain.enums.CardKind;
import com.haowujiang.sanguosha.domain.enums.PlayerAction;
import com.haowujiang.sanguosha.domain.model.BattleAiDecision;
import com.haowujiang.sanguosha.domain.model.BattleCard;
import com.haowujiang.sanguosha.domain.model.BattleState;
import com.haowujiang.sanguosha.domain.model.RagMemorySnippet;
import com.haowujiang.sanguosha.domain.service.AiDecisionDomainService;
import com.haowujiang.sanguosha.domain.service.BattleCardDomainService;
import com.haowujiang.sanguosha.domain.service.RagMemoryDomainService;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AiDecisionDomainServiceImpl implements AiDecisionDomainService {

    private final ObjectProvider<ChatLanguageModel> chatLanguageModelProvider;
    private final BattleCardDomainService battleCardDomainService;
    private final RagMemoryDomainService ragMemoryDomainService;
    private final ObjectMapper objectMapper;

    @Value("${app.ai.provider}")
    private String aiProvider;

    @Override
    public BattleAiDecision decide(BattleState state) {

        List<RagMemorySnippet> memorySnippets = searchMemorySnippets(state);
        ChatLanguageModel chatLanguageModel = chatLanguageModelProvider.getIfAvailable();

        if (chatLanguageModel != null && !"mock".equalsIgnoreCase(aiProvider)) {
            return decideByLangChain4j(state, memorySnippets, chatLanguageModel);
        }

        return fallbackDecision(state, memorySnippets);
    }

    private BattleAiDecision decideByLangChain4j(BattleState state,
                                                 List<RagMemorySnippet> memorySnippets,
                                                 ChatLanguageModel chatLanguageModel) {

        BattleAiTools tools = new BattleAiTools(state, battleCardDomainService, objectMapper);

        try {
            BattleAiPlayer aiPlayer = AiServices.builder(BattleAiPlayer.class)
                    .chatLanguageModel(chatLanguageModel)
                    .tools(tools)
                    .build();

            String answer = aiPlayer.decide(buildPrompt(state, memorySnippets));

            BattleAiDecision decision = parseDecision(state, answer);

            decision.getThoughts()
                    .add("LangChain4j 决策：" + decision.getReason());

            return decision;

        } catch (Exception e) {

            BattleAiDecision fallback = fallbackDecision(state, memorySnippets);
            fallback.getThoughts().add("LLM失败，走兜底策略");

            return fallback;
        }
    }

    private String buildPrompt(BattleState state,
                               List<RagMemorySnippet> memorySnippets) {

        return """
                当前AI回合：
                AI武将：%s
                AI技能：%s
                AI体力：%d/%d

                玩家武将：%s
                玩家体力：%d/%d

                已用杀次数：%d
                是否喝酒：%s

                RAG记忆：
                %s

                只返回JSON：
                {"action":"PLAY_CARD/END_TURN","cardId":"","reason":"","thoughts":[]}
                """.formatted(
                state.getEnemy().getName(),
                state.getEnemy().getSkillName(),
                state.getEnemy().getHp(),
                state.getEnemy().getMaxHp(),
                state.getPlayer().getName(),
                state.getPlayer().getHp(),
                state.getPlayer().getMaxHp(),
                state.getEnemy().getShaUsed(),
                state.getEnemy().isDrunk(),
                buildRagMemory(memorySnippets)
        );
    }

    private BattleAiDecision parseDecision(BattleState state, String answer) {

        if (!StringUtils.hasText(answer)) {
            return BattleAiDecision.endTurn("空回答", new ArrayList<>());
        }

        try {
            String json = extractJson(answer);
            JsonNode node = objectMapper.readTree(json);

            List<String> thoughts = parseThoughts(node);

            String actionStr = node.path("action")
                    .asText(PlayerAction.END_TURN.getValue());

            PlayerAction action = PlayerAction.fromValue(actionStr);
            String cardId = node.path("cardId").asText(null);
            String reason = node.path("reason").asText("AI决策");

            if (action == PlayerAction.PLAY_CARD
                    && StringUtils.hasText(cardId)) {

                BattleCard card = findPlayableAiCard(state, cardId);

                if (card != null) {
                    return BattleAiDecision.playCard(card.getId(), reason, thoughts);
                }

                thoughts.add("非法出牌，自动结束");
                return BattleAiDecision.endTurn("非法牌", thoughts);
            }

            return BattleAiDecision.endTurn(reason, thoughts);

        } catch (Exception e) {
            return BattleAiDecision.endTurn("解析失败", List.of(answer));
        }
    }

    private BattleCard findPlayableAiCard(BattleState state, String cardId) {

        BattleCard card = state.getEnemy().getHand().stream()
                .filter(c -> c.getId().equals(cardId))
                .findFirst()
                .orElse(null);

        if (card == null) {
            return null;
        }

        if (battleCardDomainService.canUseAs(
                state,
                state.getEnemy(),
                card,
                CardKind.SHA
        ) && battleCardDomainService.canUseSha(state, state.getEnemy(), card)) {
            return card;
        }

        if (card.getKind() == CardKind.TAO
                && state.getEnemy().getHp() < state.getEnemy().getMaxHp()) {
            return card;
        }

        if (card.getKind() == CardKind.JIU
                && !state.getEnemy().isDrunk()
                && battleCardDomainService.findAttackCard(state, state.getEnemy()) != null) {
            return card;
        }

        return null;
    }

    private BattleAiDecision fallbackDecision(BattleState state,
                                              List<RagMemorySnippet> memorySnippets) {

        List<String> thoughts = new ArrayList<>();
        thoughts.add("fallback策略");
        thoughts.add(buildRagTitleSummary(memorySnippets));

        BattleCard peach = battleCardDomainService.findFirstCard(
                state.getEnemy(),
                CardKind.TAO
        );

        if (peach != null
                && state.getEnemy().getHp() <= 2
                && state.getEnemy().getHp() < state.getEnemy().getMaxHp()) {
            return BattleAiDecision.playCard(peach.getId(), "回血", thoughts);
        }

        BattleCard attack = battleCardDomainService.findAttackCard(state, state.getEnemy());

        BattleCard wine = battleCardDomainService.findFirstCard(
                state.getEnemy(),
                CardKind.JIU
        );

        if (wine != null && attack != null && !state.getEnemy().isDrunk()) {
            return BattleAiDecision.playCard(wine.getId(), "先酒后杀", thoughts);
        }

        if (attack != null
                && battleCardDomainService.canUseSha(state, state.getEnemy(), attack)) {
            return BattleAiDecision.playCard(attack.getId(), "攻击", thoughts);
        }

        return BattleAiDecision.endTurn("无牌可用", thoughts);
    }

    private List<RagMemorySnippet> searchMemorySnippets(BattleState state) {
        try {
            return new ArrayList<>(ragMemoryDomainService.searchForBattle(state));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String buildRagMemory(List<RagMemorySnippet> list) {
        if (list == null || list.isEmpty()) {
            return "无RAG";
        }

        return list.stream()
                .map(s -> s.getTitle() + ":" + s.getContent())
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");
    }

    private String buildRagTitleSummary(List<RagMemorySnippet> list) {
        if (list == null || list.isEmpty()) {
            return "无记忆";
        }

        return list.stream()
                .limit(3)
                .map(RagMemorySnippet::getTitle)
                .reduce((a, b) -> a + "," + b)
                .orElse("");
    }

    private List<String> parseThoughts(JsonNode node) {
        List<String> list = new ArrayList<>();
        JsonNode n = node.path("thoughts");

        if (n.isArray()) {
            n.forEach(i -> list.add(i.asText()));
        } else {
            list.add(n.asText());
        }

        return list;
    }

    private String extractJson(String text) {
        int s = text.indexOf('{');
        int e = text.lastIndexOf('}');
        return (s >= 0 && e > s) ? text.substring(s, e + 1) : text;
    }
}