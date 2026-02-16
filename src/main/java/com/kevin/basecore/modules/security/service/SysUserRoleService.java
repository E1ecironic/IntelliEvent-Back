package com.kevin.basecore.modules.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kevin.basecore.modules.security.entity.SysUserRole;

import java.util.List;

public interface SysUserRoleService extends IService<SysUserRole> {

    boolean assignRoles(String userId, List<String> roleIds);

    List<String> listRoleIds(String userId);
}
