package com.kevin.intellieventback.domin.dto;

import com.kevin.intellieventback.domin.entity.Activities;
import com.kevin.intellieventback.domin.entity.ActivityBudget;
import com.kevin.intellieventback.domin.entity.ActivityParticipant;
import com.kevin.intellieventback.domin.entity.ActivityRisk;
import com.kevin.intellieventback.domin.entity.ActivitySchedule;
import com.kevin.intellieventback.domin.entity.ActivitySupplier;
import com.kevin.intellieventback.domin.entity.ActivityTask;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Schema(name = "ActivityDetailDTO", description = "活动详情聚合数据")
public class ActivityDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "活动基本信息")
    private Activities activity;

    @Schema(description = "日程列表")
    private List<ActivitySchedule> schedules;

    @Schema(description = "任务列表")
    private List<ActivityTask> tasks;

    @Schema(description = "预算列表")
    private List<ActivityBudget> budgets;

    @Schema(description = "参与人员列表")
    private List<ActivityParticipant> participants;

    @Schema(description = "供应商列表")
    private List<ActivitySupplier> suppliers;

    @Schema(description = "风险列表")
    private List<ActivityRisk> risks;
}