package com.kevin.basecore.modules.ai.controller;

import com.kevin.basecore.common.domin.Result;
import com.kevin.basecore.modules.ai.entity.AiKnowledge;
import com.kevin.basecore.modules.ai.model.AiRagSearchRequest;
import com.kevin.basecore.modules.ai.service.AiRagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ai/rag")
@Tag(name = "AI知识库")
@RequiredArgsConstructor
public class AiRagController {

    private final AiRagService aiRagService;

    @PostMapping("/search")
    @Operation(summary = "检索知识库")
    public Result<List<AiKnowledge>> search(@RequestBody AiRagSearchRequest request) {
        int limit = request != null && request.getLimit() != null ? request.getLimit() : 3;
        String query = request != null ? request.getQuery() : null;
        return Result.success(aiRagService.search(query, limit));
    }

    @PostMapping("/save")
    @Operation(summary = "保存知识")
    public Result<Boolean> save(@RequestBody AiKnowledge knowledge) {
        return Result.success(aiRagService.saveKnowledge(knowledge));
    }
}
