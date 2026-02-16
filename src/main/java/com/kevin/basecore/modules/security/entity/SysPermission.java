package com.kevin.basecore.modules.security.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kevin.basecore.common.domin.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
@Schema(description = "系统权限")
public class SysPermission extends BaseEntity {

    @Schema(description = "父权限ID")
    private String parentId;

    @Schema(description = "权限名称")
    private String name;

    @Schema(description = "权限编码")
    private String code;

    @Schema(description = "类型：MENU/BUTTON/API")
    private String type;

    @Schema(description = "路由路径")
    private String path;

    @Schema(description = "前端组件")
    private String component;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "请求方法")
    private String method;

    @Schema(description = "接口路径")
    private String api;

    @Schema(description = "是否显示：1-显示 0-隐藏")
    private Byte visible;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态：1-正常 0-禁用")
    private Byte status;

    @TableField(exist = false)
    @Schema(description = "子权限")
    private List<SysPermission> children;
}
