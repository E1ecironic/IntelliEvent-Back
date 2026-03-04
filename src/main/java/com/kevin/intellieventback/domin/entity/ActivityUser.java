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

@Getter
@Setter
@ToString
@TableName("activity_user")
@Schema(name = "ActivityUser", description = "活动用户关联表")
public class ActivityUser extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("activity_id")
    @Schema(description = "活动ID")
    private String activityId;

    @TableField("user_id")
    @Schema(description = "用户ID")
    private String userId;

    @TableField("role_type")
    @Schema(description = "角色类型：1-创建者 2-参与者 3-负责人")
    private Integer roleType;
}
