package com.kevin.basecore.modules.ai.service.impl;

import com.kevin.basecore.modules.ai.client.AiChatClient;
import com.kevin.basecore.modules.ai.client.AiChatClientRequest;
import com.kevin.basecore.modules.ai.client.AiChatClientResponse;
import com.kevin.basecore.modules.ai.entity.AiKnowledge;
import com.kevin.basecore.modules.ai.enums.AiProviderType;
import com.kevin.basecore.modules.ai.model.AiChatRequest;
import com.kevin.basecore.modules.ai.model.AiChatResponse;
import com.kevin.basecore.modules.ai.model.AiMessage;
import com.kevin.basecore.modules.ai.model.AiProviderConfig;
import com.kevin.basecore.modules.ai.service.AiChatService;
import com.kevin.basecore.modules.ai.service.AiConfigService;
import com.kevin.basecore.modules.ai.service.AiContextService;
import com.kevin.basecore.modules.ai.service.AiRagService;
import com.kevin.basecore.modules.ai.service.AiRngService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {

    private final List<AiChatClient> clients;
    private final AiConfigService aiConfigService;
    private final AiContextService aiContextService;
    private final AiRngService aiRngService;
    private final AiRagService aiRagService;

    @Override
    public AiChatResponse chat(AiChatRequest request) {
        AiChatRequest safeRequest = request != null ? request : new AiChatRequest();
        AiProviderType providerType = AiProviderType.from(safeRequest.getProvider());
        if (safeRequest.getProvider() == null) {
            providerType = aiConfigService.getDefaultProvider();
        }
        AiProviderConfig config = aiConfigService.getProviderConfig(providerType, safeRequest.getProvider());
        String model = StringUtils.hasText(safeRequest.getModel()) ? safeRequest.getModel() : config.getModel();
        List<AiMessage> messages = new ArrayList<>();
        String contextId = safeRequest.getContextId();
        boolean useContext = safeRequest.getUseContext() == null || safeRequest.getUseContext();
        if (useContext && !StringUtils.hasText(contextId)) {
            contextId = aiRngService.generateId();
        }
        if (useContext && StringUtils.hasText(contextId)) {
            messages.addAll(aiContextService.getContextMessages(contextId, safeRequest.getUserId()));
        }
        if (safeRequest.getRagEnabled() != null && safeRequest.getRagEnabled()) {
            String ragQuery = StringUtils.hasText(safeRequest.getRagQuery()) ? safeRequest.getRagQuery() : extractLastUserMessage(safeRequest.getMessages());
            int topK = safeRequest.getRagTopK() != null ? safeRequest.getRagTopK() : 3;
            List<AiKnowledge> knowledgeList = aiRagService.search(ragQuery, topK);
            if (!knowledgeList.isEmpty()) {
                AiMessage ragMessage = new AiMessage();
                ragMessage.setRole("system");
                ragMessage.setContent(buildRagContent(knowledgeList));
                messages.add(ragMessage);
            }
        }
        if (StringUtils.hasText(safeRequest.getSystemPrompt())) {
            AiMessage systemMessage = new AiMessage();
            systemMessage.setRole("system");
            systemMessage.setContent(safeRequest.getSystemPrompt());
            messages.add(systemMessage);
        }
        if (safeRequest.getMessages() != null) {
            messages.addAll(safeRequest.getMessages());
        }
        AiChatClient client = selectClient(providerType);
        AiChatClientRequest clientRequest = new AiChatClientRequest();
        clientRequest.setModel(model);
        clientRequest.setMessages(messages);
        clientRequest.setTemperature(safeRequest.getTemperature() != null ? safeRequest.getTemperature() : config.getTemperature());
        clientRequest.setMaxTokens(safeRequest.getMaxTokens() != null ? safeRequest.getMaxTokens() : config.getMaxTokens());
        clientRequest.setTimeoutMs(safeRequest.getTimeoutMs() != null ? safeRequest.getTimeoutMs() : config.getTimeoutMs());
        clientRequest.setStream(Boolean.FALSE);
        clientRequest.setSeed(safeRequest.getSeed());
        AiChatClientResponse clientResponse = client.chat(clientRequest, config);
        AiMessage assistantMessage = new AiMessage();
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(clientResponse.getText());
        if (useContext && StringUtils.hasText(contextId)) {
            List<AiMessage> appended = new ArrayList<>();
            if (safeRequest.getMessages() != null) {
                appended.addAll(safeRequest.getMessages());
            }
            appended.add(assistantMessage);
            aiContextService.appendContextMessages(contextId, safeRequest.getUserId(), appended);
        }
        AiChatResponse response = new AiChatResponse();
        response.setRequestId(aiRngService.generateId());
        response.setProvider(StringUtils.hasText(config.getProviderKey()) ? config.getProviderKey() : providerType.name());
        response.setModel(model);
        response.setText(clientResponse.getText());
        response.setContextId(contextId);
        response.setUsage(clientResponse.getUsage());
        response.setRaw(clientResponse.getRaw());
        return response;
    }

    private AiChatClient selectClient(AiProviderType type) {
        Map<AiProviderType, AiChatClient> map = new HashMap<>();
        for (AiChatClient client : clients) {
            map.put(client.getType(), client);
        }
        AiChatClient selected = map.get(type);
        if (selected == null) {
            return map.values().stream().findFirst().orElseThrow();
        }
        return selected;
    }

    private String extractLastUserMessage(List<AiMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return "";
        }
        for (int i = messages.size() - 1; i >= 0; i--) {
            AiMessage message = messages.get(i);
            if (message != null && "user".equalsIgnoreCase(message.getRole()) && StringUtils.hasText(message.getContent())) {
                return message.getContent();
            }
        }
        return "";
    }

    private String buildRagContent(List<AiKnowledge> knowledgeList) {
        StringBuilder builder = new StringBuilder();
        builder.append("以下是参考资料：");
        for (AiKnowledge knowledge : knowledgeList) {
            builder.append("\n- ");
            if (StringUtils.hasText(knowledge.getTitle())) {
                builder.append(knowledge.getTitle()).append("：");
            }
            if (StringUtils.hasText(knowledge.getContent())) {
                builder.append(knowledge.getContent());
            }
        }
        return builder.toString();
    }
}
