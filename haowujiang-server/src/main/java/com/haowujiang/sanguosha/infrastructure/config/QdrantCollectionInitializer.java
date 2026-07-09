package com.haowujiang.sanguosha.infrastructure.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Qdrant collection 初始化器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QdrantCollectionInitializer implements ApplicationRunner {

    private final ObjectProvider<EmbeddingModel> embeddingModelProvider;
    private final HaowujiangAiProperties properties;

    @Override
    public void run(ApplicationArguments args) {
        EmbeddingModel embeddingModel = embeddingModelProvider.getIfAvailable();
        if (embeddingModel == null) {
            return;
        }

        HaowujiangAiProperties.QdrantProperties qdrant = properties.getQdrant();
        try (QdrantClient qdrantClient = createClient(qdrant)) {
            Boolean exists = qdrantClient.collectionExistsAsync(qdrant.getCollectionName()).get();
            if (Boolean.TRUE.equals(exists)) {
                return;
            }
            VectorParams vectorParams = VectorParams.newBuilder()
                    .setSize(resolveDimension(embeddingModel))
                    .setDistance(Distance.Cosine)
                    .build();
            qdrantClient.createCollectionAsync(qdrant.getCollectionName(), vectorParams).get();
            log.info("Qdrant collection {} created", qdrant.getCollectionName());
        } catch (Exception exception) {
            log.warn("Qdrant collection init skipped: {}", exception.getMessage());
        }
    }

    private QdrantClient createClient(HaowujiangAiProperties.QdrantProperties qdrant) {
        QdrantGrpcClient.Builder builder = QdrantGrpcClient.newBuilder(
                qdrant.getHost(),
                qdrant.getPort(),
                qdrant.getUseTls()
        );
        if (StringUtils.hasText(qdrant.getApiKey())) {
            builder.withApiKey(qdrant.getApiKey());
        }
        return new QdrantClient(builder.build());
    }

    private long resolveDimension(EmbeddingModel embeddingModel) {
        Integer configuredDimension = properties.getEmbedding().getDimension();
        if (configuredDimension != null && configuredDimension > 0) {
            return configuredDimension;
        }
        return embeddingModel.dimension();
    }
}
