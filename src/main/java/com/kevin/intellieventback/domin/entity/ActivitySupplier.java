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
@TableName("activity_suppliers")
@Schema(name = "ActivitySupplier", description = "活动供应商表")
public class ActivitySupplier extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("activity_id")
    @Schema(description = "活动ID")
    private String activityId;

    @TableField("name")
    @Schema(description = "供应商名称")
    private String name;

    @TableField("contact")
    @Schema(description = "联系人")
    private String contact;

    @TableField("phone")
    @Schema(description = "联系电话")
    private String phone;

    @TableField("email")
    @Schema(description = "邮箱")
    private String email;

    @TableField("service_type")
    @Schema(description = "服务类型")
    private String serviceType;

    @TableField("address")
    @Schema(description = "地址")
    private String address;

    @TableField("remark")
    @Schema(description = "备注")
    private String remark;
}