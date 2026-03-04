package com.kevin.intellieventback.controller;

import com.kevin.basecore.common.domin.Result;
import com.kevin.intellieventback.domin.entity.ActivityBudget;
import com.kevin.intellieventback.service.ActivityBudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/activity-budgets")
@Tag(name = "活动预算管理")
public class ActivityBudgetController {

    @Autowired
    private ActivityBudgetService activityBudgetService;

    @GetMapping("/activity/{activityId}")
    @Operation(summary = "根据活动ID查询预算列表")
    public Result<List<ActivityBudget>> getBudgetsByActivityId(
            @Parameter(description = "活动ID", required = true) @PathVariable String activityId) {
        List<ActivityBudget> budgets = activityBudgetService.getBudgetsByActivityId(activityId);
        return Result.success(budgets);
    }

    @GetMapping("/activity/{activityId}/statistics")
    @Operation(summary = "获取预算统计信息")
    public Result<Map<String, Object>> getBudgetStatistics(
            @Parameter(description = "活动ID", required = true) @PathVariable String activityId) {
        Map<String, Object> statistics = activityBudgetService.getBudgetStatistics(activityId);
        return Result.success(statistics);
    }

    @GetMapping("/activity/{activityId}/total")
    @Operation(summary = "获取已确认预算总额")
    public Result<BigDecimal> getTotalBudget(
            @Parameter(description = "活动ID", required = true) @PathVariable String activityId) {
        BigDecimal total = activityBudgetService.getTotalBudgetByActivityId(activityId);
        return Result.success(total);
    }

    @PostMapping
    @Operation(summary = "新增预算项")
    public Result<Boolean> save(@RequestBody ActivityBudget budget) {
        boolean result = activityBudgetService.saveBudget(budget);
        return Result.success(result);
    }

    @PutMapping
    @Operation(summary = "修改预算项")
    public Result<Boolean> update(@RequestBody ActivityBudget budget) {
        boolean result = activityBudgetService.updateById(budget);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除预算项")
    public Result<Boolean> delete(
            @Parameter(description = "预算项ID", required = true) @PathVariable String id) {
        boolean result = activityBudgetService.removeById(id);
        return Result.success(result);
    }
}