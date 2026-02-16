package com.kevin.basecore.modules.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.kevin.basecore.common.domin.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_permission")
@Schema(description = "角色权限关联")
public class SysRolePermission extends BaseEntity {

    @Schema(description = "角色ID")
    private String roleId;

    @Schema(description = "权限ID")
    private String permissionId;
}
