package com.kevin.basecore.modules.ai.service.impl;

import com.kevin.basecore.modules.ai.enums.AiProviderType;
import com.kevin.basecore.modules.ai.model.AiProviderConfig;
import com.kevin.basecore.modules.ai.service.AiConfigService;
import com.kevin.basecore.modules.system.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AiConfigServiceImpl implements AiConfigService {

    private final SysConfigService sysConfigService;
    private final Environment environment;

    @Override
    public AiProviderType getDefaultProvider() {
        String value = getValue("ai.provider.default", "ollama");
        return AiProviderType.from(value);
    }

    @Override
    public AiProviderConfig getProviderConfig(AiProviderType type) {
        return getProviderConfig(type, null);
    }

    @Override
    public AiProviderConfig getProviderConfig(AiProviderType type, String providerKey) {
        if (type == AiProviderType.OLLAMA) {
            return buildOllamaConfig();
        }
        String normalizedKey = normalizeProviderKey(providerKey);
        if (!StringUtils.hasText(normalizedKey)) {
            normalizedKey = "openai";
        }
        AiProviderConfig config = buildOpenAiCompatConfig(normalizedKey);
        if (!"openai".equals(normalizedKey) && !hasEssentialCompatConfig(config)) {
            return buildOpenAiCompatConfig("openai");
        }
        return config;
    }

    @Override
    public long getContextTtlSeconds() {
        return getLong("ai.context.ttl-seconds", 3600L);
    }

    @Override
    public int getContextMaxMessages() {
        return getInt("ai.context.max-messages", 20);
    }

    @Override
    public String getContextKeyPrefix() {
        return getValue("ai.context.key-prefix", "ai:ctx:");
    }

    @Override
    public int getRngIdLength() {
        return getInt("ai.rng.id-length", 16);
    }

    @Override
    public String getRngCharset() {
        return getValue("ai.rng.charset", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
    }

    private AiProviderConfig buildOllamaConfig() {
        AiProviderConfig config = new AiProviderConfig();
        config.setProviderKey("ollama");
        config.setBaseUrl(getValue("ai.provider.ollama.base-url", "http://localhost:11434"));
        config.setModel(getValue("ai.provider.ollama.model", "qwen3:8b"));
        config.setTemperature(getDouble("ai.provider.ollama.temperature", 0.7d));
        config.setMaxTokens(getInt("ai.provider.ollama.max-tokens", 1024));
        config.setTimeoutMs(getLong("ai.provider.ollama.timeout-ms", 60000L));
        return config;
    }

    private AiProviderConfig buildOpenAiCompatConfig(String providerKey) {
        AiProviderConfig config = new AiProviderConfig();
        config.setProviderKey(providerKey);
        String prefix = "ai.provider." + providerKey + ".";
        config.setBaseUrl(getValue(prefix + "base-url", getValue("ai.provider.openai.base-url", "https://api.openai.com")));
        config.setApiKey(getValue(prefix + "api-key", getValue("ai.provider.openai.api-key", "")));
        config.setModel(getValue(prefix + "model", getValue("ai.provider.openai.model", "gpt-4o-mini")));
        config.setTemperature(getDouble(prefix + "temperature", getDouble("ai.provider.openai.temperature", 0.7d)));
        config.setMaxTokens(getInt(prefix + "max-tokens", getInt("ai.provider.openai.max-tokens", 1000)));
        config.setTimeoutMs(getLong(prefix + "timeout-ms", getLong("ai.provider.openai.timeout-ms", 60000L)));
        config.setChatPath(getValue(prefix + "chat-path", getValue("ai.provider.openai.chat-path", "/v1/chat/completions")));
        config.setApiKeyHeader(getValue(prefix + "api-key-header", getValue("ai.provider.openai.api-key-header", "Authorization")));
        config.setApiKeyPrefix(getValue(prefix + "api-key-prefix", getValue("ai.provider.openai.api-key-prefix", "Bearer ")));
        return config;
    }

    private boolean hasEssentialCompatConfig(AiProviderConfig config) {
        return StringUtils.hasText(config.getBaseUrl()) || StringUtils.hasText(config.getApiKey()) || StringUtils.hasText(config.getModel());
    }

    private String normalizeProviderKey(String providerKey) {
        if (!StringUtils.hasText(providerKey)) {
            return null;
        }
        String normalized = providerKey.trim().toLowerCase();
        if ("openai_compat".equals(normalized) || "openai-compat".equals(normalized) || "openai".equals(normalized) || "cloud".equals(normalized)) {
            return "openai";
        }
        if ("ollama".equals(normalized)) {
            return "ollama";
        }
        return normalized.replaceAll("[^a-z0-9\\-]", "");
    }

    private String getValue(String key, String defaultValue) {
        String sysValue = sysConfigService.getValue(key);
        if (StringUtils.hasText(sysValue)) {
            return sysValue;
        }
        String envValue = environment.getProperty(key);
        if (StringUtils.hasText(envValue)) {
            return envValue;
        }
        return defaultValue;
    }

    private int getInt(String key, int defaultValue) {
        String value = getValue(key, null);
        if (!StringUtils.hasText(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private long getLong(String key, long defaultValue) {
        String value = getValue(key, null);
        if (!StringUtils.hasText(value)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private double getDouble(String key, double defaultValue) {
        String value = getValue(key, null);
        if (!StringUtils.hasText(value)) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }
}
