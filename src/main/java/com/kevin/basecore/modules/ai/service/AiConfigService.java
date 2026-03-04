package com.kevin.basecore.modules.ai.service;

import com.kevin.basecore.modules.ai.enums.AiProviderType;
import com.kevin.basecore.modules.ai.model.AiProviderConfig;

public interface AiConfigService {
    AiProviderType getDefaultProvider();
    AiProviderConfig getProviderConfig(AiProviderType type);
    AiProviderConfig getProviderConfig(AiProviderType type, String providerKey);
    long getContextTtlSeconds();
    int getContextMaxMessages();
    String getContextKeyPrefix();
    int getRngIdLength();
    String getRngCharset();
}
