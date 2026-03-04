package com.kevin.basecore.modules.ai.service;

import com.kevin.basecore.modules.ai.model.AiChatRequest;
import com.kevin.basecore.modules.ai.model.AiChatResponse;

public interface AiChatService {
    AiChatResponse chat(AiChatRequest request);
}
