package com.kevin.intellieventback.domin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kevin.basecore.common.domin.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("activity_risks")
@Schema(name = "ActivityRisk", description = "活动风险表")
public class ActivityRisk extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("activity_id")
    @Schema(description = "活动ID")
    private String activityId;

    @TableField("risk_type")
    @Schema(description = "风险类型：天气、安全、供应商、人员、其他")
    private String riskType;

    @TableField("description")
    @Schema(description = "风险描述")
    private String description;

    @TableField("level")
    @Schema(description = "风险等级：高、中、低")
    private String level;

    @TableField("probability")
    @Schema(description = "发生概率：高、中、低")
    private String probability;

    @TableField("impact")
    @Schema(description = "影响程度：高、中、低")
    private String impact;

    @TableField("emergency_plan")
    @Schema(description = "应急预案")
    private String emergencyPlan;

    @TableField("status")
    @Schema(description = "状态：未处理、处理中、已解决")
    private String status;
}