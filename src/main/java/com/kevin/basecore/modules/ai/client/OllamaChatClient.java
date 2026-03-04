package com.kevin.basecore.modules.ai.client;

import com.kevin.basecore.modules.ai.client.dto.OllamaChatRequest;
import com.kevin.basecore.modules.ai.client.dto.OllamaChatResponse;
import com.kevin.basecore.modules.ai.enums.AiProviderType;
import com.kevin.basecore.modules.ai.model.AiProviderConfig;
import com.kevin.basecore.modules.ai.model.AiUsage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OllamaChatClient implements AiChatClient {

    private final WebClient.Builder webClientBuilder;

    @Override
    public AiProviderType getType() {
        return AiProviderType.OLLAMA;
    }

    @Override
    public AiChatClientResponse chat(AiChatClientRequest request, AiProviderConfig config) {
        WebClient client = webClientBuilder.baseUrl(config.getBaseUrl()).build();
        OllamaChatRequest body = new OllamaChatRequest();
        body.setModel(request.getModel());
        body.setMessages(request.getMessages());
        body.setStream(Boolean.FALSE);
        Map<String, Object> options = new HashMap<>();
        if (request.getTemperature() != null) {
            options.put("temperature", request.getTemperature());
        }
        if (request.getMaxTokens() != null) {
            options.put("num_predict", request.getMaxTokens());
        }
        if (request.getSeed() != null) {
            options.put("seed", request.getSeed());
        }
        body.setOptions(options);
        OllamaChatResponse response = client.post()
                .uri("/api/chat")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(OllamaChatResponse.class)
                .timeout(Duration.ofMillis(request.getTimeoutMs()))
                .onErrorResume(ex -> Mono.empty())
                .block();
        AiChatClientResponse result = new AiChatClientResponse();
        if (response != null && response.getMessage() != null) {
            result.setText(response.getMessage().getContent());
        } else {
            result.setText("");
        }
        result.setUsage(new AiUsage());
        result.setRaw(response);
        return result;
    }
}
