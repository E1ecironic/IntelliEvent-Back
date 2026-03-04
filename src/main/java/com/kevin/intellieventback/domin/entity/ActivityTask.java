package com.kevin.intellieventback.domin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kevin.basecore.common.domin.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("activity_tasks")
@Schema(name = "ActivityTask", description = "活动任务表")
public class ActivityTask extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("activity_id")
    @Schema(description = "活动ID")
    private String activityId;

    @TableField("name")
    @Schema(description = "任务名称")
    private String name;

    @TableField("responsible")
    @Schema(description = "负责人ID")
    private String responsible;

    @TableField(exist = false)
    @Schema(description = "负责人姓名")
    private String responsibleName;

    @TableField("deadline")
    @Schema(description = "截止日期")
    private LocalDate deadline;

    @TableField("status")
    @Schema(description = "状态：未开始、进行中、已完成")
    private String status;

    @TableField("priority")
    @Schema(description = "优先级：高、中、低")
    private String priority;

    @TableField("description")
    @Schema(description = "任务描述")
    private String description;
}