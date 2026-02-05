package com.kevin.intellieventback.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kevin.intellieventback.domin.entity.UserOrganization;
import com.kevin.intellieventback.mapper.UserOrganizationMapper;
import com.kevin.intellieventback.service.UserOrganizationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户组织关系表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2026-02-05
 */
@Service
public class UserOrganizationServiceImpl extends ServiceImpl<UserOrganizationMapper, UserOrganization> implements UserOrganizationService {

    @Override
    public IPage<UserOrganization> pagelist(UserOrganization entity) {
        return null;
    }
}
