package com.kevin.basecore.modules.ai.model;

import lombok.Data;

@Data
public class AiProviderConfig {
    private String providerKey;
    private String baseUrl;
    private String apiKey;
    private String model;
    private Double temperature;
    private Integer maxTokens;
    private Long timeoutMs;
    private String chatPath;
    private String apiKeyHeader;
    private String apiKeyPrefix;
}
