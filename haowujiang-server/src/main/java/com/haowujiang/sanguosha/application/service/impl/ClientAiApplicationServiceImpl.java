package com.haowujiang.sanguosha.application.service.impl;

import cn.hutool.core.util.IdUtil;
import com.haowujiang.sanguosha.application.service.ClientAiApplicationService;
import com.haowujiang.sanguosha.application.vo.ai.request.ClientAiChatReqVo;
import com.haowujiang.sanguosha.application.vo.ai.response.ClientAiChatRespVo;
import com.haowujiang.sanguosha.application.vo.ai.response.RagReferenceBasicVo;
import com.haowujiang.sanguosha.domain.exception.BusinessException;
import com.haowujiang.sanguosha.infrastructure.config.HaowujiangAiProperties;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

/**
 * 客户端 AI 应用服务实现
 */
@Service
@RequiredArgsConstructor
public class ClientAiApplicationServiceImpl implements ClientAiApplicationService {

    private static final String METADATA_GENERAL_CODE = "generalCode";
    private static final String METADATA_DOCUMENT_ID = "documentId";
    private static final String METADATA_TITLE = "title";

    private final ObjectProvider<ChatLanguageModel> chatLanguageModelProvider;
    private final ObjectProvider<EmbeddingModel> embeddingModelProvider;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final HaowujiangAiProperties properties;

    @Override
    public ClientAiChatRespVo chat(ClientAiChatReqVo reqVo) {
        ChatLanguageModel chatLanguageModel = chatLanguageModelProvider.getIfAvailable();
        EmbeddingModel embeddingModel = embeddingModelProvider.getIfAvailable();
        if (chatLanguageModel == null || embeddingModel == null) {
            throw new BusinessException("未配置大模型或向量模型，无法进行 RAG 问答");
        }

        // 1 将用户问题转换成向量
        Embedding questionEmbedding = embeddingModel.embed(reqVo.getQuestion()).content();

        // 2 用问题向量到 Qdrant 检索相似知识片段
        EmbeddingSearchRequest searchRequest = buildSearchRequest(reqVo, questionEmbedding);
        EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(searchRequest);

        // 3.将检索到的RAG知识拼到prompt中
        String prompt = buildPrompt(reqVo.getQuestion(), searchResult.matches());

        // 4.生成答案
        String generate = chatLanguageModel.generate(prompt);

        // 5.返回答案
        ClientAiChatRespVo respVo = new ClientAiChatRespVo();
        respVo.setConversationId(resolveConversationId(reqVo.getConversationId()));
        respVo.setReferences(toReferences(searchResult.matches()));
        respVo.setAnswer(generate);
        return respVo;
    }

    private EmbeddingSearchRequest buildSearchRequest(ClientAiChatReqVo reqVo, Embedding questionEmbedding) {
        EmbeddingSearchRequest.EmbeddingSearchRequestBuilder builder = EmbeddingSearchRequest.builder()
                .queryEmbedding(questionEmbedding)
                .maxResults(resolveTopK(reqVo.getTopK()))
                .minScore(properties.getRag().getMinScore());
        if (StringUtils.hasText(reqVo.getGeneralCode())) {
            builder.filter(metadataKey(METADATA_GENERAL_CODE).isEqualTo(reqVo.getGeneralCode()));
        }
        return builder.build();
    }

    private Integer resolveTopK(Integer topK) {
        if (topK == null || topK <= 0) {
            return properties.getRag().getDefaultTopK();
        }
        return topK;
    }

    private String resolveConversationId(String conversationId) {
        return StringUtils.hasText(conversationId) ? conversationId : IdUtil.randomUUID();
    }

    private List<RagReferenceBasicVo> toReferences(List<EmbeddingMatch<TextSegment>> matches) {
        return matches.stream()
                .map(this::toReference)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private RagReferenceBasicVo toReference(EmbeddingMatch<TextSegment> match) {
        TextSegment segment = match.embedded();
        RagReferenceBasicVo vo = new RagReferenceBasicVo();
        vo.setDocumentId(parseLong(segment.metadata().getString(METADATA_DOCUMENT_ID)));
        vo.setGeneralCode(segment.metadata().getString(METADATA_GENERAL_CODE));
        vo.setTitle(segment.metadata().getString(METADATA_TITLE));
        vo.setContent(segment.text());
        vo.setScore(match.score());
        return vo;
    }

    private String buildPrompt(String question, List<EmbeddingMatch<TextSegment>> matches) {
        return """
                你是好武将平台的 AI 助手
                请只根据下方 RAG 参考资料回答问题
                如果资料不足，请明确说明资料不足，不要编造

                用户问题：
                %s

                RAG 参考资料：
                %s
                """.formatted(question, buildReferencesText(matches));
    }

    private String buildReferencesText(List<EmbeddingMatch<TextSegment>> matches) {
        if (matches == null || matches.isEmpty()) {
            return "未检索到相关资料";
        }
        return matches.stream()
                .map(match -> "- " + match.embedded().metadata().getString(METADATA_TITLE)
                        + "，相似度：" + match.score()
                        + "\n  " + match.embedded().text())
                .reduce((left, right) -> left + "\n" + right)
                .orElse("未检索到相关资料");
    }

    private Long parseLong(String value) {
        try {
            return Long.valueOf(value);
        } catch (Exception exception) {
            return null;
        }
    }
}
