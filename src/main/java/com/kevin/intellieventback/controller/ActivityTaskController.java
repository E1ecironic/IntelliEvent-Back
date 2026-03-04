package com.kevin.intellieventback.controller;

import com.kevin.basecore.common.domin.Result;
import com.kevin.intellieventback.domin.entity.ActivityTask;
import com.kevin.intellieventback.service.ActivityTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity-tasks")
@Tag(name = "活动任务管理")
public class ActivityTaskController {

    @Autowired
    private ActivityTaskService activityTaskService;

    @GetMapping("/activity/{activityId}")
    @Operation(summary = "根据活动ID查询任务列表")
    public Result<List<ActivityTask>> getTasksByActivityId(
            @Parameter(description = "活动ID", required = true) @PathVariable String activityId) {
        List<ActivityTask> tasks = activityTaskService.getTasksByActivityId(activityId);
        return Result.success(tasks);
    }

    @PostMapping
    @Operation(summary = "新增任务")
    public Result<Boolean> save(@RequestBody ActivityTask task) {
        boolean result = activityTaskService.saveTask(task);
        return Result.success(result);
    }

    @PutMapping
    @Operation(summary = "修改任务")
    public Result<Boolean> update(@RequestBody ActivityTask task) {
        boolean result = activityTaskService.updateById(task);
        return Result.success(result);
    }

    @PutMapping("/{taskId}/status")
    @Operation(summary = "更新任务状态")
    public Result<Boolean> updateStatus(
            @Parameter(description = "任务ID", required = true) @PathVariable String taskId,
            @Parameter(description = "状态", required = true) @RequestParam String status) {
        boolean result = activityTaskService.updateTaskStatus(taskId, status);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除任务")
    public Result<Boolean> delete(
            @Parameter(description = "任务ID", required = true) @PathVariable String id) {
        boolean result = activityTaskService.removeById(id);
        return Result.success(result);
    }
}