package com.kevin.basecore.modules.ai.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevin.basecore.modules.ai.model.AiMessage;
import com.kevin.basecore.modules.ai.service.AiConfigService;
import com.kevin.basecore.modules.ai.service.AiContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiContextServiceImpl implements AiContextService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final AiConfigService aiConfigService;

    @Override
    public List<AiMessage> getContextMessages(String contextId, String userId) {
        String key = buildKey(contextId, userId);
        String value = stringRedisTemplate.opsForValue().get(key);
        if (!StringUtils.hasText(value)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(value, new TypeReference<List<AiMessage>>() {});
        } catch (JsonProcessingException ex) {
            return new ArrayList<>();
        }
    }

    @Override
    public void appendContextMessages(String contextId, String userId, List<AiMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }
        List<AiMessage> existing = getContextMessages(contextId, userId);
        existing.addAll(messages);
        int maxSize = aiConfigService.getContextMaxMessages();
        if (existing.size() > maxSize) {
            existing = existing.subList(existing.size() - maxSize, existing.size());
        }
        try {
            String value = objectMapper.writeValueAsString(existing);
            String key = buildKey(contextId, userId);
            stringRedisTemplate.opsForValue().set(key, value, Duration.ofSeconds(aiConfigService.getContextTtlSeconds()));
        } catch (JsonProcessingException ex) {
            String key = buildKey(contextId, userId);
            stringRedisTemplate.delete(key);
        }
    }

    @Override
    public void clearContext(String contextId, String userId) {
        String key = buildKey(contextId, userId);
        stringRedisTemplate.delete(key);
    }

    private String buildKey(String contextId, String userId) {
        String prefix = aiConfigService.getContextKeyPrefix();
        String safeContext = StringUtils.hasText(contextId) ? contextId : "default";
        if (StringUtils.hasText(userId)) {
            return prefix + userId + ":" + safeContext;
        }
        return prefix + safeContext;
    }
}
