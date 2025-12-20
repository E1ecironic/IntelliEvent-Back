package com.kevin.intellieventback.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "活动主表管理")
    public class ActivitiesController {

@Autowired
private ActivitiesService activitiesService;

@GetMapping("/{id}")
@Operation(summary = "根据ID查询活动主表")
public Result<Activities> getById(@Parameter(description = "ID", required = true) @PathVariable Long id) {
Activities entity = activitiesService.getById(id);
return Result.success(entity);
}

@PostMapping
@Operation(summary = "新增活动主表")
public Result<Boolean> save(@RequestBody Activities entity) {
    boolean result = activitiesService.save(entity);
    return Result.success(result);
    }

    @PutMapping
    @Operation(summary = "修改活动主表")
    public Result<Boolean> update(@RequestBody Activities entity) {
        boolean result = activitiesService.updateById(entity);
        return Result.success(result);
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "删除活动主表")
        public Result<Boolean> delete(@Parameter(description = "ID", required = true) @PathVariable Long id) {
            boolean result = activitiesService.removeById(id);
            return Result.success(result);
            }

            @GetMapping("/page")
            @Operation(summary = "分页查询活动主表")
            public Result<IPage<Activities>> page(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer pageSize) {
            IPage<Activities> page = new Page<>(pageNum, pageSize);
            activitiesService.page(page);
            return Result.success(page);
            }
            }