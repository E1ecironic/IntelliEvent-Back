package com.kevin.basecore.modules.ai.service;

import com.kevin.basecore.modules.ai.entity.AiKnowledge;

import java.util.List;

public interface AiRagService {
    List<AiKnowledge> search(String query, int limit);
    boolean saveKnowledge(AiKnowledge knowledge);
}
