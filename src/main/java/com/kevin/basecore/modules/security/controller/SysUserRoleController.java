package com.kevin.basecore.modules.security.controller;

import com.kevin.basecore.common.domin.Result;
import com.kevin.basecore.modules.security.dto.UserRoleAssignDTO;
import com.kevin.basecore.modules.security.service.SysUserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys-user-role")
@Tag(name = "用户角色管理")
@RequiredArgsConstructor
public class SysUserRoleController {

    private final SysUserRoleService sysUserRoleService;

    @PostMapping("/assign-roles")
    @Operation(
            summary = "分配用户角色",
            description = "请求体示例：{ \"userId\":\"uid\",\"roleIds\":[\"r1\",\"r2\"] }，会覆盖该用户已有角色。"
    )
    public Result<Boolean> assignRoles(@RequestBody UserRoleAssignDTO dto) {
        return Result.success(sysUserRoleService.assignRoles(dto.getUserId(), dto.getRoleIds()));
    }

    @GetMapping("/{userId}/roles")
    @Operation(
            summary = "查询用户角色ID列表",
            description = "返回指定用户绑定的角色ID列表。"
    )
    public Result<List<String>> listRoleIds(@Parameter(description = "用户ID", required = true) @PathVariable String userId) {
        return Result.success(sysUserRoleService.listRoleIds(userId));
    }
}
