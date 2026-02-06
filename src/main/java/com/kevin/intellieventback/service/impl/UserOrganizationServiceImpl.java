package com.kevin.intellieventback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.kevin.intellieventback.domin.entity.UserOrganization;
import com.kevin.intellieventback.mapper.UserOrganizationMapper;
import com.kevin.intellieventback.service.UserOrganizationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户组织关系表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2026-02-05
 */
@Slf4j
@Service
public class UserOrganizationServiceImpl extends ServiceImpl<UserOrganizationMapper, UserOrganization> implements UserOrganizationService {

    @Override
    public IPage<UserOrganization> pagelist(UserOrganization entity) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserOrganization(String userId, String organizationId) {
        log.info("更新用户组织关系 - userId: {}, organizationId: {}", userId, organizationId);
        
        if (StringUtils.isBlank(organizationId)) {
            return true;
        }

        // 1. 查询现有的主组织关系
        LambdaQueryWrapper<UserOrganization> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserOrganization::getUserId, userId)
                    .eq(UserOrganization::getIsPrimary, true);
        UserOrganization existing = getOne(queryWrapper);

        if (existing != null) {
            // 如果组织 ID 没变，直接返回
            if (organizationId.equals(existing.getOrganizationId())) {
                return true;
            }
            // 2. 更新现有的主组织关系
            existing.setOrganizationId(organizationId);
            existing.setUpdatedAt(LocalDateTime.now());
            return updateById(existing);
        } else {
            // 3. 不存在则新增
            UserOrganization userOrg = new UserOrganization();
            userOrg.setUserId(userId);
            userOrg.setOrganizationId(organizationId);
            userOrg.setRoleType((byte) 1);
            userOrg.setIsPrimary(true);
            userOrg.setStatus((byte) 1);
            userOrg.setCreatedAt(LocalDateTime.now());
            userOrg.setUpdatedAt(LocalDateTime.now());
            return save(userOrg);
        }
    }
}
