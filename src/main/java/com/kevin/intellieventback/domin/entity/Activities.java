package com.kevin.intellieventback.domin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kevin.basecore.common.domin.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@TableName("activities")
@Schema(name = "Activities", description = "活动表")
public class Activities extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("name")
    @Schema(description = "活动名称")
    private String name;

    @TableField("type")
    @Schema(description = "活动类型")
    private String type;

    @TableField("date")
    @Schema(description = "活动日期")
    private LocalDate date;

    @TableField("time_range")
    @Schema(description = "时间范围")
    private String timeRange;

    @TableField("location")
    @Schema(description = "活动地点")
    private String location;

    @TableField("participants")
    @Schema(description = "参与人数")
    private Integer participants;

    @TableField("budget")
    @Schema(description = "预算")
    private BigDecimal budget;

    @TableField("status")
    @Schema(description = "状态")
    private String status;

    @TableField("responsible")
    @Schema(description = "负责人")
    private String responsible;

    @TableField(exist = false)
    @Schema(description = "负责人姓名（非数据库字段）")
    private String responsibleName;

    @TableField("description")
    @Schema(description = "活动描述")
    private String description;

    @TableField(exist = false)
    private LocalDate dateStart;

    @TableField(exist = false)
    private LocalDate dateEnd;
}
