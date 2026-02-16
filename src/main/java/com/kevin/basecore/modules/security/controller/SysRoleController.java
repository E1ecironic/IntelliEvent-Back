package com.kevin.basecore.modules.security.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.kevin.basecore.common.domin.PageResult;
import com.kevin.basecore.common.domin.Result;
import com.kevin.basecore.modules.security.dto.RolePermissionAssignDTO;
import com.kevin.basecore.modules.security.entity.SysRole;
import com.kevin.basecore.modules.security.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/sys-role")
@Tag(name = "角色管理")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @GetMapping("/{id}")
    @Operation(
            summary = "根据ID查询角色",
            description = "返回角色基础信息，包括 name/code/description/sort/status/createdAt/updatedAt。"
    )
    public Result<SysRole> getById(@Parameter(description = "ID", required = true) @PathVariable String id) {
        return Result.success(sysRoleService.getById(id));
    }

    @PostMapping
    @Operation(
            summary = "新增角色",
            description = "请求体示例：{ \"name\":\"管理员\",\"code\":\"ADMIN\",\"description\":\"系统管理员\",\"sort\":1,\"status\":1 }"
    )
    public Result<Boolean> save(@RequestBody SysRole role) {
        if (StringUtils.isNotBlank(role.getCode())) {
            LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysRole::getCode, role.getCode());
            if (sysRoleService.count(wrapper) > 0) {
                throw new RuntimeException("角色编码已存在");
            }
        }
        LocalDateTime now = LocalDateTime.now();
        if (role.getCreatedAt() == null) {
            role.setCreatedAt(now);
        }
        role.setUpdatedAt(now);
        return Result.success(sysRoleService.save(role));
    }

    @PutMapping
    @Operation(
            summary = "修改角色",
            description = "请求体需包含 id，其他字段按需更新。示例：{ \"id\":\"xxx\",\"name\":\"运营\",\"status\":1 }"
    )
    public Result<Boolean> update(@RequestBody SysRole role) {
        if (StringUtils.isNotBlank(role.getCode())) {
            LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysRole::getCode, role.getCode());
            if (StringUtils.isNotBlank(role.getId())) {
                wrapper.ne(SysRole::getId, role.getId());
            }
            if (sysRoleService.count(wrapper) > 0) {
                throw new RuntimeException("角色编码已存在");
            }
        }
        role.setUpdatedAt(LocalDateTime.now());
        return Result.success(sysRoleService.updateById(role));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "删除角色",
            description = "删除角色并清理用户角色与角色权限关联。"
    )
    public Result<Boolean> delete(@Parameter(description = "ID", required = true) @PathVariable String id) {
        return Result.success(sysRoleService.removeRoleById(id));
    }

    @PostMapping("/page")
    @Operation(
            summary = "分页查询角色",
            description = "请求体示例：{ \"pageNum\":1,\"pageSize\":10,\"name\":\"管\",\"code\":\"ADMIN\",\"status\":1 }"
    )
    public Result page(@RequestBody SysRole role) {
        IPage<SysRole> page = sysRoleService.pageList(role);
        return Result.success(PageResult.returnResult(page.getTotal(), page.getRecords()));
    }

    @PostMapping("/assign-permissions")
    @Operation(
            summary = "分配角色权限",
            description = "请求体示例：{ \"roleId\":\"roleId\",\"permissionIds\":[\"p1\",\"p2\"] }，会覆盖该角色已有权限。"
    )
    public Result<Boolean> assignPermissions(@RequestBody RolePermissionAssignDTO dto) {
        return Result.success(sysRoleService.assignPermissions(dto.getRoleId(), dto.getPermissionIds()));
    }

    @GetMapping("/{roleId}/permissions")
    @Operation(
            summary = "查询角色权限ID列表",
            description = "返回指定角色绑定的权限ID列表。"
    )
    public Result<List<String>> listPermissionIds(@Parameter(description = "角色ID", required = true) @PathVariable String roleId) {
        return Result.success(sysRoleService.listPermissionIds(roleId));
    }
}
