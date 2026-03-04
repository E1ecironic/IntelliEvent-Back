package com.kevin.basecore.modules.ai.client;

import com.kevin.basecore.modules.ai.model.AiMessage;
import lombok.Data;

import java.util.List;

@Data
public class AiChatClientRequest {
    private String model;
    private List<AiMessage> messages;
    private Double temperature;
    private Integer maxTokens;
    private Long timeoutMs;
    private Boolean stream;
    private Long seed;
}
