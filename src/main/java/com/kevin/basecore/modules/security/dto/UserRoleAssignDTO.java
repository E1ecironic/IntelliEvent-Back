package com.kevin.basecore.modules.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "用户角色分配请求")
public class UserRoleAssignDTO {

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "角色ID列表")
    private List<String> roleIds;
}
