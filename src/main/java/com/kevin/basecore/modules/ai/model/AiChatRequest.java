package com.kevin.basecore.modules.ai.model;

import lombok.Data;

import java.util.List;

@Data
public class AiChatRequest {
    private String provider;
    private String model;
    private String systemPrompt;
    private List<AiMessage> messages;
    private String contextId;
    private String userId;
    private Boolean useContext;
    private Integer maxTokens;
    private Double temperature;
    private Long timeoutMs;
    private Boolean stream;
    private Long seed;
    private Boolean ragEnabled;
    private String ragQuery;
    private Integer ragTopK;
}
