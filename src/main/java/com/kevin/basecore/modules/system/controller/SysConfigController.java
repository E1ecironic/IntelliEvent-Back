package com.kevin.basecore.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kevin.basecore.common.domin.Result;
import com.kevin.basecore.modules.system.entity.SysConfig;
import com.kevin.basecore.modules.system.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 系统配置表 前端控制器
 * </p>
 *
 * @author kevin
 * @since 2026-02-05
 */
@RestController
@RequestMapping("/sys-config")
@Tag(name = "系统配置管理")
public class SysConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    @GetMapping("/page")
    @Operation(summary = "分页查询配置")
    public Result<IPage<SysConfig>> page(SysConfig sysConfig) {
        Page<SysConfig> page = new Page<>(sysConfig.getPageNum() != null ? sysConfig.getPageNum() : 1, 
                                          sysConfig.getPageSize() != null ? sysConfig.getPageSize() : 10);
        
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        // 支持模糊查询 configKey
        if (StringUtils.hasText(sysConfig.getConfigKey())) {
            wrapper.like(SysConfig::getConfigKey, sysConfig.getConfigKey());
        }
        // 按 configKey 升序排序
        wrapper.orderByAsc(SysConfig::getConfigKey);
        
        return Result.success(sysConfigService.page(page, wrapper));
    }

    @GetMapping("/{key}")
    @Operation(summary = "根据键获取配置")
    public Result<String> getValue(@PathVariable String key) {
        return Result.success(sysConfigService.getValue(key));
    }

    @PostMapping
    @Operation(summary = "保存或更新配置")
    public Result<Boolean> saveOrUpdate(@RequestBody SysConfig sysConfig) {
        return Result.success(sysConfigService.saveOrUpdateConfig(sysConfig.getConfigKey(), sysConfig.getConfigValue(), sysConfig.getDescription()));
    }

    @DeleteMapping("/{key}")
    @Operation(summary = "根据键删除配置")
    public Result<Boolean> delete(@PathVariable String key) {
        return Result.success(sysConfigService.deleteConfig(key));
    }
}
