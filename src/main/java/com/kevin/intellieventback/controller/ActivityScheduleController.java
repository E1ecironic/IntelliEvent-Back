package com.kevin.intellieventback.controller;

import com.kevin.basecore.common.domin.Result;
import com.kevin.intellieventback.domin.entity.ActivitySchedule;
import com.kevin.intellieventback.service.ActivityScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity-schedules")
@Tag(name = "活动日程管理")
public class ActivityScheduleController {

    @Autowired
    private ActivityScheduleService activityScheduleService;

    @GetMapping("/activity/{activityId}")
    @Operation(summary = "根据活动ID查询日程列表")
    public Result<List<ActivitySchedule>> getSchedulesByActivityId(
            @Parameter(description = "活动ID", required = true) @PathVariable String activityId) {
        List<ActivitySchedule> schedules = activityScheduleService.getSchedulesByActivityId(activityId);
        return Result.success(schedules);
    }

    @PostMapping
    @Operation(summary = "新增日程")
    public Result<Boolean> save(@RequestBody ActivitySchedule schedule) {
        boolean result = activityScheduleService.saveSchedule(schedule);
        return Result.success(result);
    }

    @PutMapping
    @Operation(summary = "修改日程")
    public Result<Boolean> update(@RequestBody ActivitySchedule schedule) {
        boolean result = activityScheduleService.updateById(schedule);
        return Result.success(result);
    }

    @PutMapping("/{scheduleId}/sort")
    @Operation(summary = "更新排序")
    public Result<Boolean> updateSortOrder(
            @Parameter(description = "日程ID", required = true) @PathVariable String scheduleId,
            @Parameter(description = "排序号", required = true) @RequestParam Integer sortOrder) {
        boolean result = activityScheduleService.updateSortOrder(scheduleId, sortOrder);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除日程")
    public Result<Boolean> delete(
            @Parameter(description = "日程ID", required = true) @PathVariable String id) {
        boolean result = activityScheduleService.removeById(id);
        return Result.success(result);
    }
}