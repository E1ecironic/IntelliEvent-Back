package com.kevin.basecore.modules.ai.client;

import com.kevin.basecore.modules.ai.client.dto.OpenAiChatRequest;
import com.kevin.basecore.modules.ai.client.dto.OpenAiChatResponse;
import com.kevin.basecore.modules.ai.enums.AiProviderType;
import com.kevin.basecore.modules.ai.model.AiProviderConfig;
import com.kevin.basecore.modules.ai.model.AiUsage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OpenAiCompatChatClient implements AiChatClient {

    private final WebClient.Builder webClientBuilder;

    @Override
    public AiProviderType getType() {
        return AiProviderType.OPENAI_COMPAT;
    }

    @Override
    public AiChatClientResponse chat(AiChatClientRequest request, AiProviderConfig config) {
        WebClient client = webClientBuilder.baseUrl(config.getBaseUrl()).build();
        OpenAiChatRequest body = new OpenAiChatRequest();
        body.setModel(request.getModel());
        body.setMessages(request.getMessages());
        body.setTemperature(request.getTemperature());
        body.setMaxTokens(request.getMaxTokens());
        body.setStream(Boolean.FALSE);
        body.setSeed(request.getSeed());
        WebClient.RequestHeadersSpec<?> spec = client.post()
                .uri(StringUtils.hasText(config.getChatPath()) ? config.getChatPath() : "/v1/chat/completions")
                .bodyValue(body);
        if (StringUtils.hasText(config.getApiKey())) {
            String header = StringUtils.hasText(config.getApiKeyHeader()) ? config.getApiKeyHeader() : HttpHeaders.AUTHORIZATION;
            String prefix = StringUtils.hasText(config.getApiKeyPrefix()) ? config.getApiKeyPrefix() : "Bearer ";
            spec = spec.header(header, prefix + config.getApiKey());
        }
        OpenAiChatResponse response = spec.retrieve()
                .bodyToMono(OpenAiChatResponse.class)
                .timeout(Duration.ofMillis(request.getTimeoutMs()))
                .onErrorResume(ex -> Mono.empty())
                .block();
        AiChatClientResponse result = new AiChatClientResponse();
        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            result.setText(response.getChoices().get(0).getMessage() != null ? response.getChoices().get(0).getMessage().getContent() : "");
        } else {
            result.setText("");
        }
        if (response != null && response.getUsage() != null) {
            AiUsage usage = new AiUsage();
            usage.setPromptTokens(response.getUsage().getPrompt_tokens());
            usage.setCompletionTokens(response.getUsage().getCompletion_tokens());
            usage.setTotalTokens(response.getUsage().getTotal_tokens());
            result.setUsage(usage);
        } else {
            result.setUsage(new AiUsage());
        }
        result.setRaw(response);
        return result;
    }
}
