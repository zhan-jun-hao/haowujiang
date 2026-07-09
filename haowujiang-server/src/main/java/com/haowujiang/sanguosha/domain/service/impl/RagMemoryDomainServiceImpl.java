package com.haowujiang.sanguosha.domain.service.impl;

import com.haowujiang.sanguosha.domain.model.BattleActor;
import com.haowujiang.sanguosha.domain.model.BattleCard;
import com.haowujiang.sanguosha.domain.model.BattleState;
import com.haowujiang.sanguosha.domain.model.RagMemorySnippet;
import com.haowujiang.sanguosha.domain.service.RagMemoryDomainService;
import com.haowujiang.sanguosha.infrastructure.config.HaowujiangAiProperties;
import com.haowujiang.sanguosha.infrastructure.persistence.po.RagDocument;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

/**
 * RAG 记忆领域服务实现
 */
@Service
@RequiredArgsConstructor
public class RagMemoryDomainServiceImpl implements RagMemoryDomainService {

    private static final String METADATA_GENERAL_CODE = "generalCode";
    private static final String METADATA_DOCUMENT_ID = "documentId";
    private static final String METADATA_TITLE = "title";

    private final ObjectProvider<EmbeddingModel> embeddingModelProvider;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final HaowujiangAiProperties properties;

    @Override
    public void indexDocument(RagDocument document) {
        EmbeddingModel embeddingModel = embeddingModelProvider.getIfAvailable();
        if (embeddingModel == null || document == null) {
            return;
        }
        // 1.文本 -> 向量
        String text = buildDocumentText(document);
        Embedding embedding = embeddingModel.embed(text).content();
        // 2.文本 + 元数据
        TextSegment segment = TextSegment.from(text, Metadata.from(Map.of(
                METADATA_GENERAL_CODE, document.getGeneralCode(),
                METADATA_DOCUMENT_ID, String.valueOf(document.getId()),
                METADATA_TITLE, document.getTitle()
        )));
        // 3.存入向量数据库
        embeddingStore.add(embedding, segment);
    }

    @Override
    public List<RagMemorySnippet> searchForBattle(BattleState state) {
        EmbeddingModel embeddingModel = embeddingModelProvider.getIfAvailable();
        if (embeddingModel == null || state == null || state.getEnemy() == null) {
            return new ArrayList<>();
        }

        Embedding queryEmbedding = embeddingModel.embed(buildBattleQuery(state)).content();

        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(properties.getRag().getDefaultTopK())
                .minScore(properties.getRag().getMinScore())
                .filter(metadataKey(METADATA_GENERAL_CODE).isEqualTo(state.getEnemy().getGeneralId()))
                .build();
        EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(request);
        return searchResult.matches().stream()
                .map(this::toSnippet)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private String buildDocumentText(RagDocument document) {
        return "标题：" + document.getTitle() + "\n内容：" + document.getContent();
    }

    /**
     * 知己知彼才能百战百胜 查询对手的RAG
     *
     * @param state
     * @return
     */
    private String buildBattleQuery(BattleState state) {
        BattleActor enemy = state.getEnemy();
        BattleActor player = state.getPlayer();
        return """
                当前是武将对战 AI 出牌决策
                AI 武将：%s
                AI 技能：%s
                AI 体力：%d/%d
                AI 手牌：%s
                玩家武将：%s
                玩家体力：%d/%d
                本回合已使用杀次数：%d
                是否处于酒状态：%s
                请检索适合当前局势的武将出牌记忆
                """.formatted(
                enemy.getName(),
                enemy.getSkillName(),
                enemy.getHp(),
                enemy.getMaxHp(),
                handSummary(enemy),
                player.getName(),
                player.getHp(),
                player.getMaxHp(),
                enemy.getShaUsed(),
                enemy.isDrunk()
        );
    }

    private String handSummary(BattleActor actor) {
        return actor.getHand().stream()
                .map(this::cardSummary)
                .reduce((left, right) -> left + "，" + right)
                .orElse("无手牌");
    }

    private String cardSummary(BattleCard card) {
        return card.getName() + "(" + card.getSuit() + card.getPoint() + ")";
    }

    private RagMemorySnippet toSnippet(EmbeddingMatch<TextSegment> match) {
        TextSegment segment = match.embedded();
        Metadata metadata = segment.metadata();
        return new RagMemorySnippet(
                parseLong(metadata.getString(METADATA_DOCUMENT_ID)),
                metadata.getString(METADATA_GENERAL_CODE),
                metadata.getString(METADATA_TITLE),
                segment.text(),
                match.score()
        );
    }

    private Long parseLong(String value) {
        try {
            return Long.valueOf(value);
        } catch (Exception exception) {
            return null;
        }
    }
}
