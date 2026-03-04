package com.kevin.intellieventback.controller;

import com.kevin.basecore.common.domin.Result;
import com.kevin.intellieventback.domin.entity.ActivityRisk;
import com.kevin.intellieventback.service.ActivityRiskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity-risks")
@Tag(name = "活动风险管理")
public class ActivityRiskController {

    @Autowired
    private ActivityRiskService activityRiskService;

    @GetMapping("/activity/{activityId}")
    @Operation(summary = "根据活动ID查询风险列表")
    public Result<List<ActivityRisk>> getRisksByActivityId(
            @Parameter(description = "活动ID", required = true) @PathVariable String activityId) {
        List<ActivityRisk> risks = activityRiskService.getRisksByActivityId(activityId);
        return Result.success(risks);
    }

    @PostMapping
    @Operation(summary = "新增风险")
    public Result<Boolean> save(@RequestBody ActivityRisk risk) {
        boolean result = activityRiskService.saveRisk(risk);
        return Result.success(result);
    }

    @PutMapping
    @Operation(summary = "修改风险")
    public Result<Boolean> update(@RequestBody ActivityRisk risk) {
        boolean result = activityRiskService.updateById(risk);
        return Result.success(result);
    }

    @PutMapping("/{riskId}/emergency-plan")
    @Operation(summary = "更新应急预案")
    public Result<Boolean> updateEmergencyPlan(
            @Parameter(description = "风险ID", required = true) @PathVariable String riskId,
            @Parameter(description = "应急预案", required = true) @RequestParam String emergencyPlan) {
        boolean result = activityRiskService.updateEmergencyPlan(riskId, emergencyPlan);
        return Result.success(result);
    }

    @PutMapping("/{riskId}/status")
    @Operation(summary = "更新风险状态")
    public Result<Boolean> updateStatus(
            @Parameter(description = "风险ID", required = true) @PathVariable String riskId,
            @Parameter(description = "状态", required = true) @RequestParam String status) {
        boolean result = activityRiskService.updateRiskStatus(riskId, status);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除风险")
    public Result<Boolean> delete(
            @Parameter(description = "风险ID", required = true) @PathVariable String id) {
        boolean result = activityRiskService.removeById(id);
        return Result.success(result);
    }
}