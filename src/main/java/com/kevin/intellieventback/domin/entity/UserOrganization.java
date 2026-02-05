package com.kevin.intellieventback.domin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kevin.basecore.common.domin.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户组织关系表
 * </p>
 *
 * @author kevin
 * @since 2026-02-05
 */
@Data
@TableName("user_organization")
@Schema(name = "UserOrganization", description = "用户组织关系表")
public class UserOrganization extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 关系ID，主键，UUID格式
     */
    @Schema(description = "关系ID，主键，UUID格式")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户ID，关联users表
     */
    @Schema(description = "用户ID，关联users表")
    private String userId;

    /**
     * 组织ID，关联organizations表
     */
    @Schema(description = "组织ID，关联organizations表")
    private String organizationId;

    /**
     * 角色类型：1-普通成员 2-管理员 3-负责人
     */
    @Schema(description = "角色类型：1-普通成员 2-管理员 3-负责人")
    private Byte roleType;

    /**
     * 在组织中的职位
     */
    @Schema(description = "在组织中的职位")
    private String position;

    /**
     * 加入组织的日期
     */
    @Schema(description = "加入组织的日期")
    private LocalDate joinDate;

    /**
     * 是否为主组织：0-否 1-是
     */
    @Schema(description = "是否为主组织：0-否 1-是")
    private Boolean isPrimary;

    /**
     * 状态：1-正常 0-已离职/移除 2-停职
     */
    @Schema(description = "状态：1-正常 0-已离职/移除 2-停职")
    private Byte status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    /**
     * 创建者用户ID
     */
    @Schema(description = "创建者用户ID")
    private String createdBy;

    /**
     * 最后更新者用户ID
     */
    @Schema(description = "最后更新者用户ID")
    private String updatedBy;

    /**
     * 备注信息
     */
    @Schema(description = "备注信息")
    private String remarks;
}
