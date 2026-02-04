package com.kevin.intellieventback.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kevin.intellieventback.domin.entity.Organizations;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 组织架构表 服务类
 * </p>
 *
 * @author kevin
 * @since 2025-12-20
 */
public interface OrganizationsService extends IService<Organizations> {

    IPage<Organizations> pagelist(Organizations entity);

    boolean saveOrganizations(Organizations entity);

    boolean updateDataById(Organizations entity);

    boolean removeDataById(String id);
}
