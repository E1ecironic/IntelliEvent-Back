package com.kevin.basecore.modules.ai.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevin.basecore.common.domin.Result;
import com.kevin.basecore.modules.ai.model.AiChatRequest;
import com.kevin.basecore.modules.ai.model.AiChatResponse;
import com.kevin.basecore.modules.ai.model.AiContextClearRequest;
import com.kevin.basecore.modules.ai.model.AiMessage;
import com.kevin.basecore.modules.ai.service.AiChatService;
import com.kevin.basecore.modules.ai.service.AiContextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@Tag(name = "AI公共能力")
@RequiredArgsConstructor
public class AiChatController {

    private final AiChatService aiChatService;
    private final AiContextService aiContextService;
    private final ObjectMapper objectMapper;

    @PostMapping("/chat")
    @Operation(summary = "AI对话")
    public Result<AiChatResponse> chat(@RequestBody AiChatRequest request) {
        return Result.success(aiChatService.chat(request));
    }

    @PostMapping("/context/clear")
    @Operation(summary = "清理上下文")
    public Result<Boolean> clear(@RequestBody AiContextClearRequest request) {
        if (request != null) {
            aiContextService.clearContext(request.getContextId(), request.getUserId());
        }
        return Result.success(true);
    }

    @PostMapping("/plan/structured")
    @Operation(summary = "生成结构化活动方案")
    public Result<Map<String, Object>> generateStructuredPlan(@RequestBody PlanStructuredRequest request) {
        AiChatRequest chatRequest = new AiChatRequest();
        chatRequest.setProvider(request.getProvider());
        chatRequest.setModel(request.getModel());
        chatRequest.setTemperature(request.getTemperature());
        chatRequest.setMaxTokens(request.getMaxTokens());
        if (request.getTimeoutMs() != null) {
            chatRequest.setTimeoutMs(request.getTimeoutMs().longValue());
        }
        chatRequest.setUseContext(false);
        chatRequest.setRagEnabled(false);
        chatRequest.setSystemPrompt(buildStructuredSystemPrompt());

        AiMessage userMessage = new AiMessage();
        userMessage.setRole("user");
        userMessage.setContent(buildStructuredUserPrompt(request));
        chatRequest.setMessages(List.of(userMessage));

        AiChatResponse response = aiChatService.chat(chatRequest);
        Map<String, Object> parsed = parseJsonToMap(response.getText());
        if (parsed == null) {
            parsed = new HashMap<>();
            parsed.put("rawText", response.getText());
            parsed.put("parseSuccess", false);
        } else {
            parsed.putIfAbsent("parseSuccess", true);
        }
        if (response.getContextId() != null) {
            parsed.putIfAbsent("contextId", response.getContextId());
        }
        return Result.success(parsed);
    }

    private String buildStructuredSystemPrompt() {
        return """
            你是活动策划助手，只能输出严格的JSON对象，不要输出任何额外文本。
            JSON字段要求：
            name: 活动名称
            type: 活动类型
            date: 活动日期，格式 YYYY-MM-DD
            timeRange: 时间范围，格式 HH:mm-HH:mm
            location: 活动地点
            participants: 参与人数，数字
            budget: 预算金额，数字
            description: 活动描述
            agenda: 活动流程，字符串数组
            materials: 物料清单，字符串数组
            """;
    }

    private String buildStructuredUserPrompt(PlanStructuredRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append("请基于以下信息生成结构化活动方案：");
        if (request.getName() != null) {
            builder.append(" 活动名称=").append(request.getName()).append("。");
        }
        if (request.getType() != null) {
            builder.append(" 活动类型=").append(request.getType()).append("。");
        }
        if (request.getDate() != null) {
            builder.append(" 活动日期=").append(request.getDate()).append("。");
        }
        if (request.getLocation() != null) {
            builder.append(" 活动地点=").append(request.getLocation()).append("。");
        }
        if (request.getParticipants() != null) {
            builder.append(" 参与人数=").append(request.getParticipants()).append("。");
        }
        if (request.getBudget() != null) {
            builder.append(" 预算=").append(request.getBudget()).append("。");
        }
        if (request.getDescription() != null) {
            builder.append(" 描述=").append(request.getDescription()).append("。");
        }
        if (request.getPrompt() != null) {
            builder.append(" 额外需求=").append(request.getPrompt()).append("。");
        }
        return builder.toString();
    }

    private Map<String, Object> parseJsonToMap(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            String trimmed = text.trim();
            int startIndex = trimmed.indexOf('{');
            int endIndex = trimmed.lastIndexOf('}');
            if (startIndex >= 0 && endIndex > startIndex) {
                trimmed = trimmed.substring(startIndex, endIndex + 1);
            }
            return objectMapper.readValue(trimmed, new TypeReference<Map<String, Object>>() {});
        } catch (Exception ex) {
            return null;
        }
    }

    @Data
    public static class PlanStructuredRequest {
        private String provider;
        private String model;
        private Double temperature;
        private Integer maxTokens;
        private Integer timeoutMs;
        private String name;
        private String type;
        private String date;
        private String location;
        private Integer participants;
        private Double budget;
        private String description;
        private String prompt;
    }
}
