package com.kevin.intellieventback.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.kevin.intellieventback.entity.Organizations;
import com.kevin.intellieventback.service.OrganizationsService;
import com.kevin.basecore.common.domin.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 组织架构表 前端控制器
 * </p>
 *
 * @author kevin
 * @since 2025-12-20
 */
@Slf4j
@RestController
@RequestMapping("/organizations")
@Tag(name = "组织架构表管理")
public class OrganizationsController {

    @Autowired
    private OrganizationsService organizationsService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询组织架构表")
    public Result<Organizations> getById(@Parameter(description = "ID", required = true) @PathVariable Long id) {
        Organizations entity = organizationsService.getById(id);
        return Result.success(entity);
    }

    @PostMapping
    @Operation(summary = "新增组织架构表")
    public Result<Boolean> save(@RequestBody Organizations entity) {
        boolean result = organizationsService.saveOrganizations(entity);
        return Result.success(result);
    }

    @PutMapping
    @Operation(summary = "修改组织架构表")
    public Result<Boolean> update(@RequestBody Organizations entity) {
        boolean result = organizationsService.updateDataById(entity);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除组织架构表")
    public Result<Boolean> delete(@Parameter(description = "ID", required = true) @PathVariable Long id) {
        boolean result = organizationsService.removeDataById(id);
        return Result.success(result);
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询组织架构表")
    public Result<IPage<Organizations>> page(@RequestBody Organizations entity) {
        IPage<Organizations> page = organizationsService.pagelist(entity);
        return Result.success(page);
    }
}