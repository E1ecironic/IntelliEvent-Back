package com.kevin.basecore.modules.ai.model;

import lombok.Data;

@Data
public class AiUsage {
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer totalTokens;
}
