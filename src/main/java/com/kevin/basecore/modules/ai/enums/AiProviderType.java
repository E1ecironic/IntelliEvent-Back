package com.kevin.basecore.modules.ai.enums;

import org.springframework.util.StringUtils;

public enum AiProviderType {
    OLLAMA,
    OPENAI_COMPAT;

    public static AiProviderType from(String value) {
        if (!StringUtils.hasText(value)) {
            return OLLAMA;
        }
        String normalized = value.trim().toUpperCase();
        if ("OLLAMA".equals(normalized)) {
            return OLLAMA;
        }
        if ("OPENAI".equals(normalized) || "OPENAI_COMPAT".equals(normalized) || "CLOUD".equals(normalized)) {
            return OPENAI_COMPAT;
        }
        return OPENAI_COMPAT;
    }
}
