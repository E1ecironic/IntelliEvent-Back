package com.kevin.intellieventback.controller;

import com.kevin.basecore.common.domin.Result;
import com.kevin.intellieventback.domin.entity.ActivityParticipant;
import com.kevin.intellieventback.service.ActivityParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity-participants")
@Tag(name = "活动人员管理")
public class ActivityParticipantController {

    @Autowired
    private ActivityParticipantService activityParticipantService;

    @GetMapping("/activity/{activityId}")
    @Operation(summary = "根据活动ID查询参与人员列表")
    public Result<List<ActivityParticipant>> getParticipantsByActivityId(
            @Parameter(description = "活动ID", required = true) @PathVariable String activityId) {
        List<ActivityParticipant> participants = activityParticipantService.getParticipantsByActivityId(activityId);
        return Result.success(participants);
    }

    @PostMapping
    @Operation(summary = "新增参与人员")
    public Result<Boolean> save(@RequestBody ActivityParticipant participant) {
        boolean result = activityParticipantService.saveParticipant(participant);
        return Result.success(result);
    }

    @PutMapping
    @Operation(summary = "修改参与人员")
    public Result<Boolean> update(@RequestBody ActivityParticipant participant) {
        boolean result = activityParticipantService.updateById(participant);
        return Result.success(result);
    }

    @PutMapping("/{participantId}/status")
    @Operation(summary = "更新参与人员状态")
    public Result<Boolean> updateStatus(
            @Parameter(description = "人员ID", required = true) @PathVariable String participantId,
            @Parameter(description = "状态", required = true) @RequestParam String status) {
        boolean result = activityParticipantService.updateParticipantStatus(participantId, status);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除参与人员")
    public Result<Boolean> delete(
            @Parameter(description = "人员ID", required = true) @PathVariable String id) {
        boolean result = activityParticipantService.removeById(id);
        return Result.success(result);
    }
}