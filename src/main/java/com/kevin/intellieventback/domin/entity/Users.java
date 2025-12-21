package com.kevin.intellieventback.domin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kevin.basecore.common.domin.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author kevin
 * @since 2025-12-21
 */
@Getter
@Setter
@ToString
@TableName("users")
@Schema(name = "Users", description = "用户表")
public class Users extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID，主键
     */
    @Schema(description = "用户ID，主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名，唯一
     */
    @TableField("username")
    @Schema(description = "用户名，唯一")
    private String username;

    /**
     * 邮箱，唯一
     */
    @TableField("email")
    @Schema(description = "邮箱，唯一")
    private String email;

    /**
     * 密码哈希
     */
    @TableField("password_hash")
    @Schema(description = "密码哈希")
    private String passwordHash;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    @Schema(description = "真实姓名")
    private String realName;

    /**
     * 头像URL
     */
    @TableField("avatar_url")
    @Schema(description = "头像URL")
    private String avatarUrl;

    /**
     * 手机号
     */
    @TableField("phone")
    @Schema(description = "手机号")
    private String phone;

    /**
     * 职位
     */
    @TableField("position")
    @Schema(description = "职位")
    private String position;

    /**
     * 状态：1-正常 0-禁用 2-待激活
     */
    @TableField("status")
    @Schema(description = "状态：1-正常 0-禁用 2-待激活")
    private Byte status;

    /**
     * 最后登录时间
     */
    @TableField("last_login_at")
    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginAt;

    /**
     * 用户个性化设置（JSON格式）
     */
    @TableField("settings")
    @Schema(description = "用户个性化设置（JSON格式）")
    private String settings;

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

    /**
     * 密码盐值
     */
    @TableField("salt")
    @Schema(description = "密码盐值")
    private String salt;
}
