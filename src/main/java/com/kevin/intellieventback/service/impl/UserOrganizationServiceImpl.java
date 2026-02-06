package com.kevin.intellieventback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kevin.intellieventback.domin.entity.Organizations;
import com.kevin.intellieventback.domin.entity.UserOrganization;
import com.kevin.intellieventback.domin.entity.Users;
import com.kevin.intellieventback.mapper.UserOrganizationMapper;
import com.kevin.intellieventback.mapper.UsersMapper;
import com.kevin.intellieventback.service.OrganizationsService;
import com.kevin.intellieventback.service.UserOrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户组织关系表 服务实现类
 * </p>
 *
 * @author kevin
 * @since 2026-02-05
 */
@Slf4j
@Service
public class UserOrganizationServiceImpl extends ServiceImpl<UserOrganizationMapper, UserOrganization> implements UserOrganizationService {

    @Autowired
    private OrganizationsService organizationsService;

    @Autowired
    private UsersMapper usersMapper;

    @Override
    public IPage<UserOrganization> pagelist(UserOrganization entity) {
        return null;
    }

    @Override
    public List<Users> listUsersByOrgId(String orgId) {
        if (StringUtils.isBlank(orgId)) {
            return Collections.emptyList();
        }

        // 1. 获取所有组织架构
        List<Organizations> allOrgs = organizationsService.list();
        Map<String, Organizations> orgMap = allOrgs.stream().collect(Collectors.toMap(Organizations::getId, o -> o));
        
        // 2. 找到目标组织及其所有下级组织ID
        Set<String> targetOrgIds = new HashSet<>();
        targetOrgIds.add(orgId);
        
        // 预处理 parent -> children 映射，优化递归查找
        Map<String, List<String>> parentToChildrenMap = allOrgs.stream()
                .filter(o -> StringUtils.isNotBlank(o.getParentId()))
                .collect(Collectors.groupingBy(Organizations::getParentId, 
                        Collectors.mapping(Organizations::getId, Collectors.toList())));
        
        collectChildOrgIds(orgId, parentToChildrenMap, targetOrgIds);

        if (targetOrgIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 查询这些组织下的用户关联关系
        LambdaQueryWrapper<UserOrganization> uoWrapper = new LambdaQueryWrapper<>();
        uoWrapper.in(UserOrganization::getOrganizationId, targetOrgIds);
        uoWrapper.eq(UserOrganization::getStatus, 1); // 状态正常
        List<UserOrganization> userOrgs = list(uoWrapper);
        
        if (userOrgs.isEmpty()) {
            return Collections.emptyList();
        }

        // 4. 提取用户ID
        Set<String> userIds = userOrgs.stream().map(UserOrganization::getUserId).collect(Collectors.toSet());
        
        // 分组：UserId -> List<UserOrganization>
        Map<String, List<UserOrganization>> userOrgGroup = userOrgs.stream()
                .collect(Collectors.groupingBy(UserOrganization::getUserId));

        // 5. 查询用户详情
        List<Users> users = usersMapper.selectBatchIds(userIds);
        
        // 6. 填充 orgPathName
        for (Users user : users) {
            List<UserOrganization> uos = userOrgGroup.get(user.getId());
            if (uos != null && !uos.isEmpty()) {
                // 优先找主组织，且该主组织必须在我们的 targetOrgIds 范围内
                String displayOrgId = uos.stream()
                        .filter(uo -> Boolean.TRUE.equals(uo.getIsPrimary()))
                        .map(UserOrganization::getOrganizationId)
                        .findFirst()
                        .orElse(uos.get(0).getOrganizationId());

                user.setOrganizationId(displayOrgId);
                user.setOrgPathName(buildOrgPathName(displayOrgId, orgMap));
            }
        }

        return users;
    }

    private void collectChildOrgIds(String parentId, Map<String, List<String>> parentToChildrenMap, Set<String> result) {
        List<String> children = parentToChildrenMap.get(parentId);
        if (children != null) {
            for (String childId : children) {
                result.add(childId);
                collectChildOrgIds(childId, parentToChildrenMap, result);
            }
        }
    }

    private String buildOrgPathName(String orgId, Map<String, Organizations> orgMap) {
        List<String> names = new ArrayList<>();
        Organizations current = orgMap.get(orgId);
        while (current != null) {
            names.add(0, current.getName());
            current = orgMap.get(current.getParentId());
        }
        return String.join("/", names);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserOrganization(String userId, String organizationId) {
        log.info("更新用户组织关系 - userId: {}, organizationId: {}", userId, organizationId);
        
        if (StringUtils.isBlank(organizationId)) {
            return true;
        }

        // 1. 查询现有的主组织关系
        LambdaQueryWrapper<UserOrganization> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserOrganization::getUserId, userId)
                    .eq(UserOrganization::getIsPrimary, true);
        UserOrganization existing = getOne(queryWrapper);

        if (existing != null) {
            // 如果组织 ID 没变，直接返回
            if (organizationId.equals(existing.getOrganizationId())) {
                return true;
            }
            // 2. 更新现有的主组织关系
            existing.setOrganizationId(organizationId);
            existing.setUpdatedAt(LocalDateTime.now());
            return updateById(existing);
        } else {
            // 3. 不存在则新增
            UserOrganization userOrg = new UserOrganization();
            userOrg.setUserId(userId);
            userOrg.setOrganizationId(organizationId);
            userOrg.setRoleType((byte) 1);
            userOrg.setIsPrimary(true);
            userOrg.setStatus((byte) 1);
            userOrg.setCreatedAt(LocalDateTime.now());
            userOrg.setUpdatedAt(LocalDateTime.now());
            return save(userOrg);
        }
    }
}
