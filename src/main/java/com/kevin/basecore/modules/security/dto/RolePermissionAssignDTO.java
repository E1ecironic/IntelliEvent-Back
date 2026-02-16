package com.kevin.basecore.modules.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "角色权限分配请求")
public class RolePermissionAssignDTO {

    @Schema(description = "角色ID")
    private String roleId;

    @Schema(description = "权限ID列表")
    private List<String> permissionIds;
}
