package com.kevin.basecore.modules.ai.client.dto;

import com.kevin.basecore.modules.ai.model.AiMessage;
import lombok.Data;

import java.util.List;

@Data
public class OpenAiChatRequest {
    private String model;
    private List<AiMessage> messages;
    private Double temperature;
    private Integer maxTokens;
    private Boolean stream;
    private Long seed;
}
