package com.kevin.intellieventback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kevin.intellieventback.domin.entity.Organizations;
import com.kevin.intellieventback.mapper.OrganizationsMapper;
import com.kevin.intellieventback.service.OrganizationsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 组织架构表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2025-12-20
 */
@Service
public class OrganizationsServiceImpl extends ServiceImpl<OrganizationsMapper, Organizations> implements OrganizationsService {

    @Override
    public IPage<Organizations> pagelist(Organizations entity) {
        // 创建分页对象
        IPage<Organizations> page = new Page<>(entity.getPageNum(), entity.getPageSize());

        // 创建查询条件 - 先查询顶级组织
        LambdaQueryWrapper<Organizations> queryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件（非空判断）
        if (StringUtils.isNotBlank(entity.getName())) {
            queryWrapper.like(Organizations::getName, entity.getName());
        }
        if (StringUtils.isNotBlank(entity.getCode())) {
            queryWrapper.like(Organizations::getCode, entity.getCode());
        }
        if (entity.getStatus() != null) {
            queryWrapper.eq(Organizations::getStatus, entity.getStatus());
        }
        if (entity.getParentId() != null) {
            queryWrapper.eq(Organizations::getParentId, entity.getParentId());
        } else {
            // 默认查询顶级组织（parent_id为null）
            queryWrapper.isNull(Organizations::getParentId);
        }
        if (entity.getLevel() != null) {
            queryWrapper.eq(Organizations::getLevel, entity.getLevel());
        }
        if (entity.getManagerId() != null) {
            queryWrapper.eq(Organizations::getManagerId, entity.getManagerId());
        }

        // 执行分页查询 - 先获取顶级组织
        IPage<Organizations> rawPage = baseMapper.selectPage(page, queryWrapper);
        List<Organizations> topOrganizations = rawPage.getRecords();

        if (CollectionUtils.isEmpty(topOrganizations)) {
            return rawPage;
        }

        // 获取所有相关组织数据（包括所有子组织）
        List<Organizations> allOrganizations = getAllRelatedOrganizations(topOrganizations, entity);

        // 构建树形结构
        List<Organizations> treeList = buildTreeStructure(topOrganizations, allOrganizations);

        // 创建新的分页结果
        IPage<Organizations> resultPage = new Page<>(entity.getPageNum(), entity.getPageSize());
        resultPage.setRecords(treeList);
        resultPage.setTotal(rawPage.getTotal());
        resultPage.setCurrent(rawPage.getCurrent());
        resultPage.setSize(rawPage.getSize());

        return resultPage;
    }

    /**
     * 获取所有相关组织数据（顶级组织及其所有后代）
     */
    private List<Organizations> getAllRelatedOrganizations(List<Organizations> topOrganizations, Organizations queryCondition) {
        // 收集所有顶级组织的ID
        Set<String> allOrgIds = new HashSet<>();
        Queue<Organizations> queue = new LinkedList<>(topOrganizations);

        while (!queue.isEmpty()) {
            Organizations org = queue.poll();
            allOrgIds.add(org.getId());

            // 查询直接子组织
            LambdaQueryWrapper<Organizations> childWrapper = new LambdaQueryWrapper<>();
            childWrapper.eq(Organizations::getParentId, org.getId());

            // 应用相同的查询条件
            if (queryCondition.getStatus() != null) {
                childWrapper.eq(Organizations::getStatus, queryCondition.getStatus());
            }
            if (StringUtils.isNotBlank(queryCondition.getName())) {
                childWrapper.like(Organizations::getName, queryCondition.getName());
            }

            List<Organizations> children = baseMapper.selectList(childWrapper);
            if (!CollectionUtils.isEmpty(children)) {
                queue.addAll(children);
            }
        }

        // 一次性查询所有相关组织
        if (!allOrgIds.isEmpty()) {
            LambdaQueryWrapper<Organizations> allWrapper = new LambdaQueryWrapper<>();
            allWrapper.in(Organizations::getId, allOrgIds);

            // 按层级排序，确保父节点在前
            allWrapper.orderByAsc(Organizations::getLevel);

            return baseMapper.selectList(allWrapper);
        }

        return new ArrayList<>();
    }

    /**
     * 构建树形结构
     */
    private List<Organizations> buildTreeStructure(List<Organizations> topOrganizations, List<Organizations> allOrganizations) {
        // 创建ID到组织的映射
        Map<String, Organizations> orgMap = new HashMap<>();
        for (Organizations org : allOrganizations) {
            org.setChildren(new ArrayList<>()); // 初始化children列表
            orgMap.put(org.getId(), org);
        }

        // 构建树形结构
        List<Organizations> treeList = new ArrayList<>();
        for (Organizations topOrg : topOrganizations) {
            Organizations treeNode = orgMap.get(topOrg.getId());
            if (treeNode != null) {
                buildTreeRecursive(treeNode, orgMap);
                treeList.add(treeNode);
            }
        }

        return treeList;
    }

    /**
     * 递归构建树
     */
    private void buildTreeRecursive(Organizations parent, Map<String, Organizations> orgMap) {
        // 查找所有子组织
        List<Organizations> children = new ArrayList<>();
        for (Organizations org : orgMap.values()) {
            if (org.getParentId() != null && org.getParentId().equals(parent.getId())) {
                buildTreeRecursive(org, orgMap);
                children.add(org);
            }
        }

        // 按ID排序（可选）
        children.sort(Comparator.comparing(Organizations::getId));
        parent.setChildren(children);
    }

    @Override
    public boolean saveOrganizations(Organizations entity) {
        // 获取到父组织层级，设置当前组织层级
        int parentLevel = entity.getParentId() == null ? 0 : baseMapper.selectById(entity.getParentId()).getLevel();
        entity.setLevel(parentLevel + 1);
        return save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDataById(Organizations entity) {
        // 当更新状态时，同时更新子组织状态
        if (entity.getStatus() != null && entity.getId() != null) {
            // 1. 先更新当前组织
            boolean updated = updateById(entity);
            // 2. 获取所有子组织ID并批量更新
            List<String> allChildIds = getAllChildIds(entity.getId());
            if (!CollectionUtils.isEmpty(allChildIds)) {
                // 批量更新所有子组织的状态
                lambdaUpdate()
                        .set(Organizations::getStatus, entity.getStatus())
                        .in(Organizations::getId, allChildIds)
                        .update();
            }
            return updated;
        }
        return updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeDataById(String id) {
        // 验证组织是否存在
        Organizations organization = getById(id);
        if (organization == null) {
            throw new RuntimeException("组织不存在");
        }

        // 获取所有子组织ID
        List<String> allChildIds = getAllChildIds(id);

        // 将要删除的ID列表
        List<String> idsToDelete = new ArrayList<>();
        idsToDelete.add(id);
        idsToDelete.addAll(allChildIds);

        // 批量删除
        if (!CollectionUtils.isEmpty(idsToDelete)) {
            // 可选：删除前检查是否有依赖数据（如用户关联等）
            checkDependencies(idsToDelete);

            // 执行删除
            return removeByIds(idsToDelete);
        }

        return false;
    }

    /**
     * 检查组织是否有依赖数据
     */
    //todo 删除用户表关联数据
    private void checkDependencies(List<String> orgIds) {
        // 示例：检查是否有用户关联到这些组织
        // 这里需要根据你的实际业务来实现
        // 比如：检查user表中有没有organizations_id在这些ID中的用户

        // 示例代码：
        // Integer userCount = userService.countUsersByOrgIds(orgIds);
        // if (userCount > 0) {
        //     throw new RuntimeException("存在关联用户，无法删除组织");
        // }
    }

    /**
     * 获取指定组织的所有子组织ID（使用缓存优化）
     */
    private List<String> getAllChildIds(String parentId) {
        if (StringUtils.isEmpty(parentId)) {
            return new ArrayList<>();
        }

        List<String> allChildIds = new ArrayList<>();
        Queue<String> queue = new LinkedList<>();
        queue.offer(parentId);

        // 缓存已处理的组织ID，避免循环引用
        Set<String> processedIds = new HashSet<>();
        processedIds.add(parentId);

        while (!queue.isEmpty()) {
            String currentId = queue.poll();

            // 查询当前组织的直接子组织
            List<Organizations> childOrgs = lambdaQuery()
                    .select(Organizations::getId, Organizations::getName)
                    .eq(Organizations::getParentId, currentId)
                    .list();

            if (!CollectionUtils.isEmpty(childOrgs)) {
                for (Organizations child : childOrgs) {
                    String childId = child.getId();

                    // 避免重复处理
                    if (!processedIds.contains(childId)) {
                        allChildIds.add(childId);
                        queue.offer(childId);
                        processedIds.add(childId);
                    }
                }
            }
        }

        return allChildIds;
    }
}
