package com.kevin.intellieventback.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kevin.intellieventback.domin.entity.UserOrganization;
import com.baomidou.mybatisplus.extension.service.IService;

import com.kevin.intellieventback.domin.entity.Users;
import java.util.List;

/**
 * <p>
 * 用户组织关系表 服务类
 * </p>
 *
 * @author kevin
 * @since 2026-02-05
 */
public interface UserOrganizationService extends IService<UserOrganization> {

    IPage<UserOrganization> pagelist(UserOrganization entity);

    /**
     * 更新用户组织关系
     * @param userId 用户ID
     * @param organizationId 组织ID
     * @return 是否成功
     */
    boolean updateUserOrganization(String userId, String organizationId);

    /**
     * 根据组织ID查询用户列表（包含下级组织用户）
     * @param orgId 组织ID
     * @return 用户列表
     */
    List<Users> listUsersByOrgId(String orgId);
}
