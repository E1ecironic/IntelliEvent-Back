package com.kevin.basecore.modules.security.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kevin.basecore.modules.security.entity.SysPermission;

import java.util.List;
import java.util.Set;

public interface SysPermissionService extends IService<SysPermission> {

    IPage<SysPermission> pageList(SysPermission permission);

    List<SysPermission> listByRoleId(String roleId);

    List<SysPermission> listTree();

    List<SysPermission> listMenuTree();

    List<SysPermission> listByType(String type);

    boolean removePermissionById(String id);

    List<SysPermission> listMenuTreeByCodes(Set<String> codes);
}
