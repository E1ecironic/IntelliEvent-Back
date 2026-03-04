package com.kevin.intellieventback.domin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kevin.basecore.common.domin.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("activity_budgets")
@Schema(name = "ActivityBudget", description = "活动预算表")
public class ActivityBudget extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("activity_id")
    @Schema(description = "活动ID")
    private String activityId;

    @TableField("category")
    @Schema(description = "类别：场地、餐饮、物料、交通、其他")
    private String category;

    @TableField("item")
    @Schema(description = "项目名称")
    private String item;

    @TableField("quantity")
    @Schema(description = "数量")
    private Integer quantity;

    @TableField("unit_price")
    @Schema(description = "单价")
    private BigDecimal unitPrice;

    @TableField("total")
    @Schema(description = "小计")
    private BigDecimal total;

    @TableField("supplier_id")
    @Schema(description = "供应商ID")
    private String supplierId;

    @TableField("supplier_name")
    @Schema(description = "供应商名称")
    private String supplierName;

    @TableField("status")
    @Schema(description = "状态：待确认、已确认、已取消")
    private String status;

    @TableField("remark")
    @Schema(description = "备注")
    private String remark;
}