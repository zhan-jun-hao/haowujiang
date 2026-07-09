package com.haowujiang.sanguosha.infrastructure.config;

import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Sentinel 规则持久化 —— 从 Nacos 读取并订阅流控规则
 */
@Configuration
public class SentinelRuleInitializer {

    @Value("${spring.cloud.nacos.server-addr:127.0.0.1:8848}")
    private String nacosServerAddr;

    @Value("${spring.cloud.nacos.config.namespace:}")
    private String nacosNamespace;

    @Value("${spring.cloud.nacos.config.group:DEFAULT_GROUP}")
    private String nacosGroup;

    @Value("${app.seckill.sentinel-flow-data-id:sanguosha-backend-flow-rules}")
    private String flowRuleDataId;

    @PostConstruct
    public void initRules() {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, nacosServerAddr);
        if (!nacosNamespace.isEmpty()) {
            properties.setProperty(PropertyKeyConst.NAMESPACE, nacosNamespace);
        }

        NacosDataSource<List<FlowRule>> flowRuleDataSource = getListNacosDataSource(properties);
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
    }

    private NacosDataSource<List<FlowRule>> getListNacosDataSource(Properties properties) {
        ObjectMapper objectMapper = new ObjectMapper();
        return new NacosDataSource<>(
                properties, nacosGroup, flowRuleDataId,
                source -> {
                    try {
                        return objectMapper.readValue(source, new TypeReference<>() {});
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse flow rules from Nacos", e);
                    }
                }
        );
    }
}
