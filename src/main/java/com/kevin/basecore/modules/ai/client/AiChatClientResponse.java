package com.kevin.basecore.modules.ai.client;

import com.kevin.basecore.modules.ai.model.AiUsage;
import lombok.Data;

@Data
public class AiChatClientResponse {
    private String text;
    private AiUsage usage;
    private Object raw;
}
