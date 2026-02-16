package com.kevin.basecore.modules.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.kevin.basecore.modules.security.entity.SysUserRole;
import com.kevin.basecore.modules.security.mapper.SysUserRoleMapper;
import com.kevin.basecore.modules.security.service.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoles(String userId, List<String> roleIds) {
        if (StringUtils.isBlank(userId)) {
            return false;
        }
        LambdaQueryWrapper<SysUserRole> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(SysUserRole::getUserId, userId);
        remove(deleteWrapper);

        if (roleIds == null || roleIds.isEmpty()) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();
        for (String roleId : roleIds) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            ur.setCreatedAt(now);
            ur.setUpdatedAt(now);
            save(ur);
        }
        return true;
    }

    @Override
    public List<String> listRoleIds(String userId) {
        if (StringUtils.isBlank(userId)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> list = list(wrapper);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
    }
}
