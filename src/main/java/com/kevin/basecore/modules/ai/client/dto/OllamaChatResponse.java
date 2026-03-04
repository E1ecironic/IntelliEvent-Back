package com.kevin.basecore.modules.ai.client.dto;

import com.kevin.basecore.modules.ai.model.AiMessage;
import lombok.Data;

@Data
public class OllamaChatResponse {
    private AiMessage message;
    private Boolean done;
    private String model;
}
