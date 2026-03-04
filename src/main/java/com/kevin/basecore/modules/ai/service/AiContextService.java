package com.kevin.basecore.modules.ai.service;

import com.kevin.basecore.modules.ai.model.AiMessage;

import java.util.List;

public interface AiContextService {
    List<AiMessage> getContextMessages(String contextId, String userId);
    void appendContextMessages(String contextId, String userId, List<AiMessage> messages);
    void clearContext(String contextId, String userId);
}
