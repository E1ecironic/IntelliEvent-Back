package com.kevin.basecore.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kevin.basecore.modules.system.entity.SysConfig;
import com.kevin.basecore.modules.system.mapper.SysConfigMapper;
import com.kevin.basecore.modules.system.service.SysConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * <p>
 * 系统配置表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2026-02-05
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Override
    public String getValue(String key) {
        if (!StringUtils.hasText(key)) {
            return null;
        }
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, key);
        SysConfig config = getOne(wrapper);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    public String getValue(String key, String defaultValue) {
        String value = getValue(key);
        return StringUtils.hasText(value) ? value : defaultValue;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateConfig(String key, String value, String description) {
        if (!StringUtils.hasText(key)) {
            return false;
        }
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, key);
        SysConfig config = getOne(wrapper);

        if (config == null) {
            config = new SysConfig();
            config.setConfigKey(key);
            config.setCreatedAt(LocalDateTime.now());
        }
        
        config.setConfigValue(value);
        if (StringUtils.hasText(description)) {
            config.setDescription(description);
        }
        config.setUpdatedAt(LocalDateTime.now());
        
        return saveOrUpdate(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteConfig(String key) {
        if (!StringUtils.hasText(key)) {
            return false;
        }
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, key);
        return remove(wrapper);
    }
}
