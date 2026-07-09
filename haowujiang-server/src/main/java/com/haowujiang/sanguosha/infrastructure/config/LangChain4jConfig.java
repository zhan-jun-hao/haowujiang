package com.haowujiang.sanguosha.infrastructure.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LangChain4j 基础配置
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(HaowujiangAiProperties.class)
public class LangChain4jConfig {

    private final HaowujiangAiProperties properties;

    /**
     * 创建大语言模型 用来对话
     * @return
     */
    @Bean
    @ConditionalOnExpression("'${haowujiang.ai.chat.api-key:}' != ''")
    public ChatLanguageModel chatLanguageModel() {
        HaowujiangAiProperties.ModelProperties chat = properties.getChat();
        return OpenAiChatModel.builder()
                .baseUrl(chat.getBaseUrl())
                .apiKey(chat.getApiKey())
                .modelName(chat.getModelName())
                .temperature(chat.getTemperature())
                .timeout(Duration.ofSeconds(chat.getTimeoutSeconds()))
                .strictTools(true)
                .parallelToolCalls(false)
                .build();
    }

    /**
     * 创造向量模型 用来检索
     * @return
     */
    @Bean
    @ConditionalOnExpression("'${haowujiang.ai.embedding.api-key:}' != ''")
    public EmbeddingModel embeddingModel() {
        HaowujiangAiProperties.ModelProperties embedding = properties.getEmbedding();
        return OpenAiEmbeddingModel.builder()
                .baseUrl(embedding.getBaseUrl())
                .apiKey(embedding.getApiKey())
                .modelName(embedding.getModelName())
                .dimensions(embedding.getDimension())
                .timeout(Duration.ofSeconds(embedding.getTimeoutSeconds()))
                .build();
    }

    /**
     * 创建向量库存储 用来存储向量
     * @return
     */
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        HaowujiangAiProperties.QdrantProperties qdrant = properties.getQdrant();
        return QdrantEmbeddingStore.builder()
                .host(qdrant.getHost())
                .port(qdrant.getPort())
                .collectionName(qdrant.getCollectionName())
                .useTls(qdrant.getUseTls())
                .apiKey(qdrant.getApiKey())
                .build();
    }
}
