package com.kevin.intellieventback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kevin.basecore.common.domin.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 组织架构表
 * </p>
 *
 * @author kevin
 * @since 2025-12-20
 */
@Getter
@Setter
@ToString
@TableName("organizations")
@Schema(name = "Organizations", description = "组织架构表")
public class Organizations extends PageVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 组织ID，主键
     */
    @Schema(description = "组织ID，主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 组织名称
     */
    @TableField("name")
    @Schema(description = "组织名称")
    private String name;

    /**
     * 父组织ID，NULL表示顶级组织
     */
    @TableField("parent_id")
    @Schema(description = "父组织ID，NULL表示顶级组织")
    private Integer parentId;

    /**
     * 组织编码
     */
    @TableField("code")
    @Schema(description = "组织编码")
    private String code;

    /**
     * 组织层级，1为顶级
     */
    @TableField("level")
    @Schema(description = "组织层级，1为顶级")
    private Integer level;

    /**
     * 组织负责人用户ID
     */
    @TableField("manager_id")
    @Schema(description = "组织负责人用户ID")
    private Integer managerId;

    /**
     * 组织负责人名称
     */
    @TableField("manager_name")
    @Schema(description = "组织负责人名称")
    private String managerName;

    /**
     * 状态：1-正常 0-禁用
     */
    @TableField("status")
    @Schema(description = "状态：1-正常 0-禁用")
    private Byte status;

    /**
     * 联系电话
     */
    @TableField("connect_phone")
    @Schema(description = "联系电话")
    private String connectPhone;

    /**
     * 联系邮箱
     */
    @TableField("email")
    @Schema(description = "联系邮箱")
    private String email;

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
     * 子组织结构
     */
    @TableField(exist = false)
    @Schema(description = "子组织结构")
    private List<Organizations> children;

}
