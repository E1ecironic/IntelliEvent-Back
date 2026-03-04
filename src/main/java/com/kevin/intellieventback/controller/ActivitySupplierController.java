package com.kevin.intellieventback.controller;

import com.kevin.basecore.common.domin.Result;
import com.kevin.intellieventback.domin.entity.ActivitySupplier;
import com.kevin.intellieventback.service.ActivitySupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity-suppliers")
@Tag(name = "活动供应商管理")
public class ActivitySupplierController {

    @Autowired
    private ActivitySupplierService activitySupplierService;

    @GetMapping("/activity/{activityId}")
    @Operation(summary = "根据活动ID查询供应商列表")
    public Result<List<ActivitySupplier>> getSuppliersByActivityId(
            @Parameter(description = "活动ID", required = true) @PathVariable String activityId) {
        List<ActivitySupplier> suppliers = activitySupplierService.getSuppliersByActivityId(activityId);
        return Result.success(suppliers);
    }

    @PostMapping
    @Operation(summary = "新增供应商")
    public Result<Boolean> save(@RequestBody ActivitySupplier supplier) {
        boolean result = activitySupplierService.save(supplier);
        return Result.success(result);
    }

    @PutMapping
    @Operation(summary = "修改供应商")
    public Result<Boolean> update(@RequestBody ActivitySupplier supplier) {
        boolean result = activitySupplierService.updateById(supplier);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除供应商")
    public Result<Boolean> delete(
            @Parameter(description = "供应商ID", required = true) @PathVariable String id) {
        boolean result = activitySupplierService.removeById(id);
        return Result.success(result);
    }
}