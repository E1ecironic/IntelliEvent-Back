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
@TableName("activity_participants")
@Schema(name = "ActivityParticipant", description = "活动参与人员表")
public class ActivityParticipant extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("activity_id")
    @Schema(description = "活动ID")
    private String activityId;

    @TableField("user_id")
    @Schema(description = "用户ID（系统用户）")
    private String userId;

    @TableField("name")
    @Schema(description = "姓名")
    private String name;

    @TableField("department")
    @Schema(description = "部门")
    private String department;

    @TableField("role")
    @Schema(description = "角色：组织者、参与者、协助者")
    private String role;

    @TableField("contact")
    @Schema(description = "联系方式")
    private String contact;

    @TableField("email")
    @Schema(description = "邮箱")
    private String email;

    @TableField("status")
    @Schema(description = "状态：待确认、已确认、已拒绝")
    private String status;
}