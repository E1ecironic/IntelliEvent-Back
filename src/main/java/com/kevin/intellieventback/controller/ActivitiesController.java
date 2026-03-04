package com.kevin.intellieventback.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kevin.basecore.common.domin.PageResult;
import com.kevin.basecore.common.domin.Result;
import com.kevin.intellieventback.domin.entity.Activities;
import com.kevin.intellieventback.service.ActivitiesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/activities")
@Tag(name = "活动管理")
public class ActivitiesController {

    @Autowired
    private ActivitiesService activitiesService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询活动")
    public Result<Activities> getById(@Parameter(description = "ID", required = true) @PathVariable String id) {
        Activities entity = activitiesService.getById(id);
        return Result.success(entity);
    }

    @PostMapping
    @Operation(summary = "新增活动")
    public Result<Boolean> save(@RequestBody Activities entity) {
        boolean result = activitiesService.saveActivity(entity);
        return Result.success(result);
    }

    @PutMapping
    @Operation(summary = "修改活动")
    public Result<Boolean> update(@RequestBody Activities entity) {
        boolean result = activitiesService.updateById(entity);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除活动")
    public Result<Boolean> delete(@Parameter(description = "ID", required = true) @PathVariable String id) {
        boolean result = activitiesService.removeById(id);
        return Result.success(result);
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询活动")
    public Result page(@RequestBody Activities entity) {
        IPage<Activities> page = activitiesService.pageList(entity);
        return Result.success(PageResult.returnResult(page.getTotal(), page.getRecords()));
    }
}
