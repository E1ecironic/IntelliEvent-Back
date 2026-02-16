package com.kevin.basecore.modules.security.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kevin.basecore.modules.security.entity.SysRole;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {

    IPage<SysRole> pageList(SysRole role);

    boolean assignPermissions(String roleId, List<String> permissionIds);

    List<String> listPermissionIds(String roleId);

    boolean removeRoleById(String id);
}
