package com.kevin.basecore.modules.ai.client.dto;

import com.kevin.basecore.modules.ai.model.AiMessage;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class OllamaChatRequest {
    private String model;
    private List<AiMessage> messages;
    private Boolean stream;
    private Map<String, Object> options;
}
