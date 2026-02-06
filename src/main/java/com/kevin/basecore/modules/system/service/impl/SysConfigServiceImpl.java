package com.kevin.basecore.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kevin.basecore.modules.system.entity.SysConfig;
import com.kevin.basecore.modules.system.mapper.SysConfigMapper;
import com.kevin.basecore.modules.system.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String CACHE_PREFIX = "sys:config:";
    private static final long CACHE_TTL = 24; // 缓存24小时

    @Override
    public String getValue(String key) {
        if (!StringUtils.hasText(key)) {
            return null;
        }
        
        // 1. 先查Redis
        String cacheKey = CACHE_PREFIX + key;
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.hasText(cacheValue)) {
            return cacheValue;
        }

        // 2. Redis没有，查数据库
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, key);
        SysConfig config = getOne(wrapper);
        
        if (config != null && StringUtils.hasText(config.getConfigValue())) {
            // 3. 写入Redis
            redisTemplate.opsForValue().set(cacheKey, config.getConfigValue(), CACHE_TTL, TimeUnit.HOURS);
            return config.getConfigValue();
        }
        
        return null;
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
        
        boolean success = saveOrUpdate(config);
        if (success) {
            // 更新缓存：简单起见，直接删除缓存，下次读取时重新加载
            String cacheKey = CACHE_PREFIX + key;
            redisTemplate.delete(cacheKey);
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteConfig(String key) {
        if (!StringUtils.hasText(key)) {
            return false;
        }
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, key);
        boolean success = remove(wrapper);
        if (success) {
            // 删除缓存
            String cacheKey = CACHE_PREFIX + key;
            redisTemplate.delete(cacheKey);
        }
        return success;
    }
}
