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
     * 用户名，唯一
     */
    @Schema(description = "用户名，唯一")
    private String userName;

    /**
     * 邮箱，唯一
     */
    @Schema(description = "邮箱，唯一")
    private String email;

    /**
     * 密码哈希
     */
    @Schema(description = "密码哈希")
    private String passwordHash;

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名")
    private String realName;

    /**
     * 头像URL
     */
    @Schema(description = "头像URL")
    private String avatarUrl;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

    /**
     * 职位
     */
    @Schema(description = "职位")
    private String position;

    /**
     * 状态：1-正常 0-禁用 2-待激活
     */
    @Schema(description = "状态：1-正常 0-禁用 2-待激活")
    private Byte status;

    /**
     * 最后登录时间
     */
    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginAt;

    /**
     * 用户个性化设置（JSON格式）
     */
    @Schema(description = "用户个性化设置（JSON格式）")
    private String settings;

    /**
     * 密码盐值
     */
    @Schema(description = "密码盐值")
    private String salt;

    /**
     * 组织ID（非持久化字段）
     */
    @TableField(exist = false)
    @Schema(description = "组织ID")
    private String organizationId;
}
