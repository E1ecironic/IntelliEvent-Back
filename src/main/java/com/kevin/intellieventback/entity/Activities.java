package com.kevin.intellieventback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 活动主表
 * </p>
 *
 * @author kevin
 * @since 2025-12-19
 */
@Getter
@Setter
@ToString
@TableName("activities")
@ApiModel(value = "Activities对象", description = "活动主表")
public class Activities implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 活动ID，主键
     */
    @ApiModelProperty("活动ID，主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 所属组织ID
     */
    @ApiModelProperty("所属组织ID")
    @TableField("organization_id")
    private Integer organizationId;

    /**
     * 活动标题
     */
    @TableField("title")
    @ApiModelProperty("活动标题")
    private String title;

    /**
     * 活动描述
     */
    @ApiModelProperty("活动描述")
    @TableField("description")
    private String description;

    /**
     * 活动类型：年会、团建、发布会等
     */
    @TableField("type")
    @ApiModelProperty("活动类型：年会、团建、发布会等")
    private String type;

    /**
     * 封面图URL
     */
    @TableField("cover_image")
    @ApiModelProperty("封面图URL")
    private String coverImage;

    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    @ApiModelProperty("结束时间")
    private LocalDateTime endTime;

    /**
     * 活动地点
     */
    @TableField("location")
    @ApiModelProperty("活动地点")
    private String location;

    /**
     * 状态：draft-草稿 planning-策划中 in_progress-进行中 completed-已完成 cancelled-已取消
     */
    @TableField("status")
    @ApiModelProperty("状态：draft-草稿 planning-策划中 in_progress-进行中 completed-已完成 cancelled-已取消")
    private String status;

    /**
     * 总预算（元）
     */
    @ApiModelProperty("总预算（元）")
    @TableField("budget_total")
    private BigDecimal budgetTotal;

    /**
     * 已使用预算（元）
     */
    @TableField("budget_used")
    @ApiModelProperty("已使用预算（元）")
    private BigDecimal budgetUsed;

    /**
     * 负责人用户ID
     */
    @TableField("owner_id")
    @ApiModelProperty("负责人用户ID")
    private Integer ownerId;

    /**
     * 参与人数
     */
    @ApiModelProperty("参与人数")
    @TableField("participants_count")
    private Integer participantsCount;

    /**
     * 是否AI生成：1-是 0-否
     */
    @TableField("ai_generated")
    @ApiModelProperty("是否AI生成：1-是 0-否")
    private Byte aiGenerated;

    /**
     * 使用的模板ID
     */
    @TableField("template_id")
    @ApiModelProperty("使用的模板ID")
    private Integer templateId;

    /**
     * 自定义字段（JSON格式）
     */
    @TableField("custom_fields")
    @ApiModelProperty("自定义字段（JSON格式）")
    private String customFields;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
