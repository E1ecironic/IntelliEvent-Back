package com.kevin.basecore.modules.ai.model;

import lombok.Data;

@Data
public class AiContextClearRequest {
    private String contextId;
    private String userId;
}
