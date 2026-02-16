package com.kevin.basecore.modules.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.kevin.basecore.modules.security.entity.SysRole;
import com.kevin.basecore.modules.security.entity.SysRolePermission;
import com.kevin.basecore.modules.security.entity.SysUserRole;
import com.kevin.basecore.modules.security.mapper.SysRoleMapper;
import com.kevin.basecore.modules.security.mapper.SysRolePermissionMapper;
import com.kevin.basecore.modules.security.mapper.SysUserRoleMapper;
import com.kevin.basecore.modules.security.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRolePermissionMapper rolePermissionMapper;
    private final SysUserRoleMapper userRoleMapper;

    @Override
    public IPage<SysRole> pageList(SysRole role) {
        long current = role.getPageNum() == null ? 1L : role.getPageNum();
        long size = role.getPageSize() == null ? 10L : role.getPageSize();
        Page<SysRole> page = new Page<>(current, size);

        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(role.getName())) {
            wrapper.like(SysRole::getName, role.getName());
        }
        if (StringUtils.isNotBlank(role.getCode())) {
            wrapper.like(SysRole::getCode, role.getCode());
        }
        if (role.getStatus() != null) {
            wrapper.eq(SysRole::getStatus, role.getStatus());
        }
        wrapper.orderByAsc(SysRole::getSort).orderByDesc(SysRole::getCreatedAt);

        return page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPermissions(String roleId, List<String> permissionIds) {
        if (StringUtils.isBlank(roleId)) {
            return false;
        }
        LambdaQueryWrapper<SysRolePermission> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(SysRolePermission::getRoleId, roleId);
        rolePermissionMapper.delete(deleteWrapper);

        if (permissionIds == null || permissionIds.isEmpty()) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();
        for (String permissionId : permissionIds) {
            SysRolePermission rp = new SysRolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permissionId);
            rp.setCreatedAt(now);
            rp.setUpdatedAt(now);
            rolePermissionMapper.insert(rp);
        }
        return true;
    }

    @Override
    public List<String> listPermissionIds(String roleId) {
        if (StringUtils.isBlank(roleId)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<SysRolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePermission::getRoleId, roleId);
        List<SysRolePermission> list = rolePermissionMapper.selectList(wrapper);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().map(SysRolePermission::getPermissionId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeRoleById(String id) {
        if (StringUtils.isBlank(id)) {
            return false;
        }
        SysRole role = getById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        LambdaQueryWrapper<SysRolePermission> rpWrapper = new LambdaQueryWrapper<>();
        rpWrapper.eq(SysRolePermission::getRoleId, id);
        rolePermissionMapper.delete(rpWrapper);

        LambdaQueryWrapper<SysUserRole> urWrapper = new LambdaQueryWrapper<>();
        urWrapper.eq(SysUserRole::getRoleId, id);
        userRoleMapper.delete(urWrapper);

        return removeById(id);
    }
}
