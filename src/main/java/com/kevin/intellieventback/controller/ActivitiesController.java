package com.kevin.intellieventback.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.kevin.intellieventback.entity.Activities;
import com.kevin.intellieventback.service.ActivitiesService;
import com.kevin.basecore.common.domin.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 活动主表 前端控制器
 * </p>
 *
 * @author kevin
 * @since 2025-12-19
 */
@Slf4j
@RestController
@RequestMapping("/activities")
@Api(tags = "活动主表管理")
public class ActivitiesController {

    @Autowired
    private ActivitiesService activitiesService;

    @GetMapping("/{id}")
    @ApiOperation(value = "根据ID查询活动主表")
    public Result<Activities> getById(@ApiParam(value = "ID", required = true) @PathVariable Long id) {
        Activities entity = activitiesService.getById(id);
        return Result.success(entity);
    }

    @PostMapping
    @ApiOperation(value = "新增活动主表")
    public Result<Boolean> save(@RequestBody Activities entity) {
        boolean result = activitiesService.save(entity);
        return Result.success(result);
    }

    @PutMapping
    @ApiOperation(value = "修改活动主表")
    public Result<Boolean> update(@RequestBody Activities entity) {
        boolean result = activitiesService.updateById(entity);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除活动主表")
    public Result<Boolean> delete(@ApiParam(value = "ID", required = true) @PathVariable Long id) {
        boolean result = activitiesService.removeById(id);
        return Result.success(result);
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页查询活动主表")
    public Result<IPage<Activities>> page(
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<Activities> page = new Page<>(pageNum, pageSize);
        activitiesService.page(page);
        return Result.success(page);
    }
}