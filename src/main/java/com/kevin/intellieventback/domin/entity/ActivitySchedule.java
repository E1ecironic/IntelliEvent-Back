package com.kevin.intellieventback.domin.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
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
@TableName("activity_schedules")
@Schema(name = "ActivitySchedule", description = "活动日程表")
public class ActivitySchedule extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("activity_id")
    @Schema(description = "活动ID")
    private String activityId;

    @TableField("schedule_date")
    @Schema(description = "日程日期")
    private LocalDate scheduleDate;

    @TableField("start_time")
    @Schema(description = "开始时间 (HH:mm)")
    private String startTime;

    @TableField(value = "end_time", updateStrategy = FieldStrategy.ALWAYS)
    @Schema(description = "结束时间 (HH:mm)")
    private String endTime;

    @TableField("title")
    @Schema(description = "环节名称")
    private String title;

    @TableField("description")
    @Schema(description = "环节描述/备注")
    private String description;

    @TableField("location")
    @Schema(description = "地点")
    private String location;

    @TableField("responsible")
    @Schema(description = "负责人ID")
    private String responsible;

    @TableField(exist = false)
    @Schema(description = "负责人姓名")
    private String responsibleName;

    @TableField("sort_order")
    @Schema(description = "排序顺序")
    private Integer sortOrder;
}