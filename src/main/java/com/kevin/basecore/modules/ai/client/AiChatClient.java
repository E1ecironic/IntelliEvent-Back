package com.kevin.basecore.modules.ai.client;

import com.kevin.basecore.modules.ai.enums.AiProviderType;
import com.kevin.basecore.modules.ai.model.AiProviderConfig;

public interface AiChatClient {
    AiProviderType getType();
    AiChatClientResponse chat(AiChatClientRequest request, AiProviderConfig config);
}
