package com.kevin.basecore.modules.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.kevin.basecore.modules.security.entity.SysPermission;
import com.kevin.basecore.modules.security.entity.SysRolePermission;
import com.kevin.basecore.modules.security.mapper.SysPermissionMapper;
import com.kevin.basecore.modules.security.mapper.SysRolePermissionMapper;
import com.kevin.basecore.modules.security.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    private final SysRolePermissionMapper rolePermissionMapper;

    @Override
    public IPage<SysPermission> pageList(SysPermission permission) {
        long current = permission.getPageNum() == null ? 1L : permission.getPageNum();
        long size = permission.getPageSize() == null ? 10L : permission.getPageSize();
        Page<SysPermission> page = new Page<>(current, size);

        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(permission.getName())) {
            wrapper.like(SysPermission::getName, permission.getName());
        }
        if (StringUtils.isNotBlank(permission.getCode())) {
            wrapper.like(SysPermission::getCode, permission.getCode());
        }
        if (StringUtils.isNotBlank(permission.getType())) {
            wrapper.eq(SysPermission::getType, permission.getType());
        }
        if (permission.getStatus() != null) {
            wrapper.eq(SysPermission::getStatus, permission.getStatus());
        }
        wrapper.orderByAsc(SysPermission::getSort).orderByAsc(SysPermission::getId);

        return page(page, wrapper);
    }

    @Override
    public List<SysPermission> listByRoleId(String roleId) {
        if (StringUtils.isBlank(roleId)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<SysRolePermission> rpWrapper = new LambdaQueryWrapper<>();
        rpWrapper.eq(SysRolePermission::getRoleId, roleId);
        List<SysRolePermission> list = rolePermissionMapper.selectList(rpWrapper);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> permissionIds = list.stream().map(SysRolePermission::getPermissionId).collect(Collectors.toList());
        return listByIds(permissionIds);
    }

    @Override
    public List<SysPermission> listTree() {
        List<SysPermission> all = list(new LambdaQueryWrapper<SysPermission>()
                .orderByAsc(SysPermission::getSort)
                .orderByAsc(SysPermission::getId));
        return buildTree(all);
    }

    @Override
    public List<SysPermission> listMenuTree() {
        List<SysPermission> menus = list(new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getType, "MENU")
                .orderByAsc(SysPermission::getSort)
                .orderByAsc(SysPermission::getId));
        return buildTree(menus);
    }

    @Override
    public List<SysPermission> listByType(String type) {
        if (StringUtils.isBlank(type)) {
            return Collections.emptyList();
        }
        return list(new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getType, type)
                .orderByAsc(SysPermission::getSort)
                .orderByAsc(SysPermission::getId));
    }

    @Override
    public List<SysPermission> listMenuTreeByCodes(Set<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return Collections.emptyList();
        }
        if (codes.contains("*:*:*")) {
            return listMenuTree();
        }
        List<SysPermission> menus = list(new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getType, "MENU")
                .orderByAsc(SysPermission::getSort)
                .orderByAsc(SysPermission::getId));
        List<SysPermission> tree = buildTree(menus);
        return filterTree(tree, codes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removePermissionById(String id) {
        if (StringUtils.isBlank(id)) {
            return false;
        }
        SysPermission permission = getById(id);
        if (permission == null) {
            throw new RuntimeException("权限不存在");
        }
        LambdaQueryWrapper<SysRolePermission> rpWrapper = new LambdaQueryWrapper<>();
        rpWrapper.eq(SysRolePermission::getPermissionId, id);
        rolePermissionMapper.delete(rpWrapper);
        return removeById(id);
    }

    private List<SysPermission> buildTree(List<SysPermission> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, SysPermission> map = new HashMap<>();
        for (SysPermission permission : list) {
            map.put(permission.getId(), permission);
        }
        List<SysPermission> roots = new ArrayList<>();
        for (SysPermission permission : list) {
            String parentId = permission.getParentId();
            if (StringUtils.isBlank(parentId) || "0".equals(parentId)) {
                roots.add(permission);
                continue;
            }
            SysPermission parent = map.get(parentId);
            if (parent == null) {
                roots.add(permission);
                continue;
            }
            if (parent.getChildren() == null) {
                parent.setChildren(new ArrayList<>());
            }
            parent.getChildren().add(permission);
        }
        return roots;
    }

    private List<SysPermission> filterTree(List<SysPermission> nodes, Set<String> codes) {
        if (nodes == null || nodes.isEmpty()) {
            return Collections.emptyList();
        }
        List<SysPermission> result = new ArrayList<>();
        for (SysPermission node : nodes) {
            List<SysPermission> children = node.getChildren();
            List<SysPermission> filteredChildren = filterTree(children, codes);
            boolean keep = (StringUtils.isNotBlank(node.getCode()) && codes.contains(node.getCode()))
                    || (filteredChildren != null && !filteredChildren.isEmpty());
            if (keep) {
                node.setChildren(filteredChildren);
                result.add(node);
            }
        }
        return result;
    }
}
