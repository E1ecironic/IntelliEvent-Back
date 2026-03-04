package com.kevin.basecore.modules.ai.model;

import lombok.Data;

@Data
public class AiRagSearchRequest {
    private String query;
    private Integer limit;
}
