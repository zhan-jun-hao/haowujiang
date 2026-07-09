package com.haowujiang.sanguosha.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 大模型与 RAG 配置属性
 */
@Data
@ConfigurationProperties(prefix = "haowujiang.ai")
public class HaowujiangAiProperties {

    /**
     * 聊天模型配置
     */
    private ModelProperties chat = new ModelProperties();

    /**
     * 向量模型配置
     */
    private ModelProperties embedding = new ModelProperties();

    /**
     * Qdrant 向量库配置
     */
    private QdrantProperties qdrant = new QdrantProperties();

    /**
     * RAG 检索配置
     */
    private RagProperties rag = new RagProperties();

    @Data
    public static class ModelProperties {

        /**
         * OpenAI 兼容接口地址
         */
        private String baseUrl;

        /**
         * API Key
         */
        private String apiKey;

        /**
         * 模型名称
         */
        private String modelName;

        /**
         * 向量维度，聊天模型不用配置
         */
        private Integer dimension;

        /**
         * 采样温度
         */
        private Double temperature = 0.2;

        /**
         * 超时时间，单位秒
         */
        private Long timeoutSeconds = 60L;
    }

    @Data
    public static class QdrantProperties {

        /**
         * Qdrant 主机
         */
        private String host = "localhost";

        /**
         * Qdrant gRPC 端口
         */
        private Integer port = 6334;

        /**
         * Qdrant collection 名称
         */
        private String collectionName = "haowujiang_general_rag";

        /**
         * 是否使用 TLS
         */
        private Boolean useTls = false;

        /**
         * Qdrant API Key，本地一般为空
         */
        private String apiKey;
    }

    @Data
    public static class RagProperties {

        /**
         * 默认检索数量
         */
        private Integer defaultTopK = 5;

        /**
         * 最低相似度分数
         */
        private Double minScore = 0.65;
    }
}
