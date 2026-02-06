package com.kevin.basecore.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kevin.basecore.modules.system.entity.SysConfig;

/**
 * <p>
 * 系统配置表 服务类
 * </p>
 *
 * @author kevin
 * @since 2026-02-05
 */
public interface SysConfigService extends IService<SysConfig> {

    /**
     * 根据键获取配置值
     * @param key 配置键
     * @return 配置值
     */
    String getValue(String key);
    
    /**
     * 根据键获取配置值，如果不存在则返回默认值
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    String getValue(String key, String defaultValue);

    /**
     * 更新或保存配置
     * @param key 键
     * @param value 值
     * @param description 描述
     * @return 是否成功
     */
    boolean saveOrUpdateConfig(String key, String value, String description);

    /**
     * 根据键删除配置
     * @param key 键
     * @return 是否成功
     */
    boolean deleteConfig(String key);
}
