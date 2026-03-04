package com.kevin.basecore.modules.ai.client.dto;

import com.kevin.basecore.modules.ai.model.AiMessage;
import lombok.Data;

import java.util.List;

@Data
public class OpenAiChatResponse {
    private List<Choice> choices;
    private Usage usage;

    @Data
    public static class Choice {
        private AiMessage message;
    }

    @Data
    public static class Usage {
        private Integer prompt_tokens;
        private Integer completion_tokens;
        private Integer total_tokens;
    }
}
