package com.kevin.intellieventback.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.kevin.intellieventback.domin.entity.UserOrganization;
import com.kevin.intellieventback.domin.entity.Users;
import com.kevin.intellieventback.service.UserOrganizationService;
import com.kevin.basecore.common.domin.Result;
import com.kevin.basecore.common.domin.PageResult;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户组织关系表 前端控制器
 * </p>
 *
 * @author kevin
 * @since 2026-02-05
 */
@Slf4j
@RestController
@RequestMapping("/user-organization")
@Tag(name = "用户组织关系表管理")
public class UserOrganizationController {

    @Autowired
    private UserOrganizationService userOrganizationService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询用户组织关系表")
    public Result<UserOrganization> getById(@Parameter(description = "ID", required = true) @PathVariable String id) {
        UserOrganization entity = userOrganizationService.getById(id);
        return Result.success(entity);
    }

    @PostMapping
    @Operation(summary = "新增用户组织关系表")
    public Result<Boolean> save(@RequestBody UserOrganization entity) {
        boolean result = userOrganizationService.save(entity);
        return Result.success(result);
    }

    @PutMapping
    @Operation(summary = "修改用户组织关系表")
    public Result<Boolean> update(@RequestBody UserOrganization entity) {
        boolean result = userOrganizationService.updateById(entity);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户组织关系表")
    public Result<Boolean> delete(@Parameter(description = "ID", required = true) @PathVariable String id) {
        boolean result = userOrganizationService.removeById(id);
        return Result.success(result);
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询用户组织关系表")
    public Result page(@RequestBody UserOrganization entity) {
        IPage<UserOrganization> page = userOrganizationService.pagelist(entity);
        return Result.success(PageResult.returnResult(page.getTotal(), page.getRecords()));
    }

    @GetMapping("/list-users-by-org/{orgId}")
    @Operation(summary = "根据组织ID查询用户列表（包含下级组织用户）")
    public Result<List<Users>> listUsersByOrgId(@Parameter(description = "组织ID", required = true) @PathVariable String orgId) {
        List<Users> users = userOrganizationService.listUsersByOrgId(orgId);
        return Result.success(users);
    }
}