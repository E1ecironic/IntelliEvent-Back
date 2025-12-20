package com.kevin.intellieventback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "Activities", description = "活动主表")
public class Activities implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 活动ID，主键
     */
    @Schema(description = "活动ID，主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 所属组织ID
     */
    @TableField("organization_id")
    @Schema(description = "所属组织ID")
    private Integer organizationId;

    /**
     * 活动标题
     */
    @TableField("title")
    @Schema(description = "活动标题")
    private String title;

    /**
     * 活动描述
     */
    @TableField("description")
    @Schema(description = "活动描述")
    private String description;

    /**
     * 活动类型：年会、团建、发布会等
     */
    @TableField("type")
    @Schema(description = "活动类型：年会、团建、发布会等")
    private String type;

    /**
     * 封面图URL
     */
    @TableField("cover_image")
    @Schema(description = "封面图URL")
    private String coverImage;

    /**
     * 开始时间
     */
    @TableField("start_time")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    /**
     * 活动地点
     */
    @TableField("location")
    @Schema(description = "活动地点")
    private String location;

    /**
     * 状态：draft-草稿 planning-策划中 in_progress-进行中 completed-已完成 cancelled-已取消
     */
    @TableField("status")
    @Schema(description = "状态：draft-草稿 planning-策划中 in_progress-进行中 completed-已完成 cancelled-已取消")
    private String status;

    /**
     * 总预算（元）
     */
    @TableField("budget_total")
    @Schema(description = "总预算（元）")
    private BigDecimal budgetTotal;

    /**
     * 已使用预算（元）
     */
    @TableField("budget_used")
    @Schema(description = "已使用预算（元）")
    private BigDecimal budgetUsed;

    /**
     * 负责人用户ID
     */
    @TableField("owner_id")
    @Schema(description = "负责人用户ID")
    private Integer ownerId;

    /**
     * 参与人数
     */
    @Schema(description = "参与人数")
    @TableField("participants_count")
    private Integer participantsCount;

    /**
     * 是否AI生成：1-是 0-否
     */
    @TableField("ai_generated")
    @Schema(description = "是否AI生成：1-是 0-否")
    private Byte aiGenerated;

    /**
     * 使用的模板ID
     */
    @TableField("template_id")
    @Schema(description = "使用的模板ID")
    private Integer templateId;

    /**
     * 自定义字段（JSON格式）
     */
    @TableField("custom_fields")
    @Schema(description = "自定义字段（JSON格式）")
    private String customFields;

    /**
     * 创建时间
     */
    @TableField("created_at")
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
