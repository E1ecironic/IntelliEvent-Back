package com.kevin.basecore.modules.ai.model;

import lombok.Data;

@Data
public class AiChatResponse {
    private String requestId;
    private String provider;
    private String model;
    private String text;
    private String contextId;
    private AiUsage usage;
    private Object raw;
}
