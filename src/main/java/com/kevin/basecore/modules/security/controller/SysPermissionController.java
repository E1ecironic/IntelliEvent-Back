package com.kevin.basecore.modules.security.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.kevin.basecore.common.domin.PageResult;
import com.kevin.basecore.common.domin.Result;
import com.kevin.basecore.modules.security.SecurityConstants;
import com.kevin.basecore.modules.security.entity.SysPermission;
import com.kevin.basecore.modules.security.model.LoginUser;
import com.kevin.basecore.modules.security.service.SysPermissionService;
import com.kevin.basecore.modules.security.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/sys-permission")
@Tag(name = "权限管理")
@RequiredArgsConstructor
public class SysPermissionController {

    private final SysPermissionService sysPermissionService;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @GetMapping("/{id}")
    @Operation(
            summary = "根据ID查询权限",
            description = "返回权限基础信息，包括 name/code/type/path/component/icon/method/api/visible/sort/status/createdAt/updatedAt。"
    )
    public Result<SysPermission> getById(@Parameter(description = "ID", required = true) @PathVariable String id) {
        return Result.success(sysPermissionService.getById(id));
    }

    @PostMapping
    @Operation(
            summary = "新增权限",
            description = "type=MENU 时必须提供 path 与 component。示例：{ \"parentId\":null,\"name\":\"系统管理\",\"code\":\"sys:manage\",\"type\":\"MENU\",\"path\":\"/system\",\"component\":\"Layout\",\"icon\":\"setting\",\"visible\":1,\"sort\":1,\"status\":1 }"
    )
    public Result<Boolean> save(@RequestBody SysPermission permission) {
        if (StringUtils.isNotBlank(permission.getCode())) {
            LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysPermission::getCode, permission.getCode());
            if (sysPermissionService.count(wrapper) > 0) {
                throw new RuntimeException("权限编码已存在");
            }
        }
        if ("MENU".equalsIgnoreCase(permission.getType())) {
            if (StringUtils.isBlank(permission.getPath()) || StringUtils.isBlank(permission.getComponent())) {
                throw new RuntimeException("菜单权限必须配置路由路径与组件");
            }
        }
        LocalDateTime now = LocalDateTime.now();
        if (permission.getCreatedAt() == null) {
            permission.setCreatedAt(now);
        }
        permission.setUpdatedAt(now);
        return Result.success(sysPermissionService.save(permission));
    }

    @PutMapping
    @Operation(
            summary = "修改权限",
            description = "请求体需包含 id，type=MENU 时必须提供 path 与 component。示例：{ \"id\":\"pid\",\"name\":\"用户管理\",\"visible\":1 }"
    )
    public Result<Boolean> update(@RequestBody SysPermission permission) {
        if (StringUtils.isNotBlank(permission.getCode())) {
            LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysPermission::getCode, permission.getCode());
            if (StringUtils.isNotBlank(permission.getId())) {
                wrapper.ne(SysPermission::getId, permission.getId());
            }
            if (sysPermissionService.count(wrapper) > 0) {
                throw new RuntimeException("权限编码已存在");
            }
        }
        if (StringUtils.isNotBlank(permission.getId())) {
            SysPermission existing = sysPermissionService.getById(permission.getId());
            if (existing == null) {
                throw new RuntimeException("权限不存在");
            }
            String type = StringUtils.isNotBlank(permission.getType()) ? permission.getType() : existing.getType();
            if ("MENU".equalsIgnoreCase(type)) {
                String path = StringUtils.isNotBlank(permission.getPath()) ? permission.getPath() : existing.getPath();
                String component = StringUtils.isNotBlank(permission.getComponent()) ? permission.getComponent() : existing.getComponent();
                if (StringUtils.isBlank(path) || StringUtils.isBlank(component)) {
                    throw new RuntimeException("菜单权限必须配置路由路径与组件");
                }
            }
        }
        permission.setUpdatedAt(LocalDateTime.now());
        return Result.success(sysPermissionService.updateById(permission));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "删除权限",
            description = "删除权限并清理角色权限关联。"
    )
    public Result<Boolean> delete(@Parameter(description = "ID", required = true) @PathVariable String id) {
        return Result.success(sysPermissionService.removePermissionById(id));
    }

    @PostMapping("/page")
    @Operation(
            summary = "分页查询权限",
            description = "请求体示例：{ \"pageNum\":1,\"pageSize\":10,\"name\":\"用户\",\"code\":\"user\",\"type\":\"MENU\",\"status\":1 }"
    )
    public Result page(@RequestBody SysPermission permission) {
        IPage<SysPermission> page = sysPermissionService.pageList(permission);
        return Result.success(PageResult.returnResult(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/role/{roleId}")
    @Operation(
            summary = "根据角色获取权限列表",
            description = "返回角色绑定的权限对象列表（非树）。"
    )
    public Result<List<SysPermission>> listByRole(@Parameter(description = "角色ID", required = true) @PathVariable String roleId) {
        return Result.success(sysPermissionService.listByRoleId(roleId));
    }

    @GetMapping("/tree")
    @Operation(
            summary = "获取权限树",
            description = "按 parentId 组装的权限树，用于角色授权页面。"
    )
    public Result<List<SysPermission>> tree() {
        return Result.success(sysPermissionService.listTree());
    }

    @GetMapping("/menu-tree")
    @Operation(
            summary = "获取菜单权限树",
            description = "仅返回 type=MENU 的权限树，用于前端路由/菜单渲染。"
    )
    public Result<List<SysPermission>> menuTree() {
        return Result.success(sysPermissionService.listMenuTree());
    }

    @GetMapping("/user-menu-tree")
    @Operation(
            summary = "获取当前用户菜单树",
            description = "基于当前登录用户权限编码过滤的菜单树，用于前端动态路由。"
    )
    public Result<List<SysPermission>> userMenuTree(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = null;
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser principal) {
            loginUser = principal;
        }
        if (loginUser == null) {
            String header = request.getHeader(SecurityConstants.HEADER_STRING);
            if (StringUtils.isNotBlank(header) && header.toLowerCase().startsWith(SecurityConstants.TOKEN_PREFIX.toLowerCase())) {
                String token = header.substring(SecurityConstants.TOKEN_PREFIX.length()).trim();
                if (StringUtils.isNotBlank(token) && token.split("\\.").length == 3) {
                    try {
                        String userName = jwtUtil.getUsernameFromToken(token);
                        if (StringUtils.isNotBlank(userName)) {
                            UserDetails details = userDetailsService.loadUserByUsername(userName);
                            if (details instanceof LoginUser loadedUser) {
                                loginUser = loadedUser;
                            }
                        }
                    } catch (Exception ex) {
                        return Result.success(Collections.emptyList());
                    }
                }
            }
        }
        if (loginUser == null) {
            return Result.success(Collections.emptyList());
        }
        Set<String> codes = loginUser.getPermissions();
        return Result.success(sysPermissionService.listMenuTreeByCodes(codes));
    }

    @GetMapping("/buttons")
    @Operation(
            summary = "获取按钮权限列表",
            description = "返回 type=BUTTON 权限列表，用于前端按钮指令控制。"
    )
    public Result<List<SysPermission>> listButtons() {
        return Result.success(sysPermissionService.listByType("BUTTON"));
    }

    @GetMapping("/apis")
    @Operation(
            summary = "获取接口权限列表",
            description = "返回 type=API 权限列表，用于后端接口鉴权配置或前端展示。"
    )
    public Result<List<SysPermission>> listApis() {
        return Result.success(sysPermissionService.listByType("API"));
    }
}
