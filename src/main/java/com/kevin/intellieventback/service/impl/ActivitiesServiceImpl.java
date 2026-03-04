package com.kevin.intellieventback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kevin.basecore.modules.security.utils.SecurityUtils;
import com.kevin.intellieventback.domin.entity.Activities;
import com.kevin.intellieventback.domin.entity.ActivityUser;
import com.kevin.intellieventback.mapper.ActivitiesMapper;
import com.kevin.intellieventback.mapper.ActivityUserMapper;
import com.kevin.intellieventback.service.ActivitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivitiesServiceImpl extends ServiceImpl<ActivitiesMapper, Activities> implements ActivitiesService {

    @Autowired
    private ActivityUserMapper activityUserMapper;

    @Override
    public IPage<Activities> pageList(Activities entity) {
        long current = entity.getPageNum() == null ? 1L : entity.getPageNum();
        long size = entity.getPageSize() == null ? 10L : entity.getPageSize();
        IPage<Activities> page = new Page<>(current, size);

        LambdaQueryWrapper<Activities> wrapper = new LambdaQueryWrapper<>();
        
        // 权限控制：如果不是管理员，只查自己的活动
        if (!SecurityUtils.isAdmin()) {
            String userId = SecurityUtils.getUserId();
            List<ActivityUser> relations = activityUserMapper.selectList(
                new LambdaQueryWrapper<ActivityUser>().eq(ActivityUser::getUserId, userId)
            );
            List<String> activityIds = relations.stream()
                .map(ActivityUser::getActivityId)
                .collect(Collectors.toList());
            
            if (activityIds.isEmpty()) {
                return page; // 没有关联活动，返回空页
            }
            wrapper.in(Activities::getId, activityIds);
        }

        if (StringUtils.isNotBlank(entity.getName())) {
            wrapper.like(Activities::getName, entity.getName());
        }
        if (StringUtils.isNotBlank(entity.getType())) {
            wrapper.eq(Activities::getType, entity.getType());
        }
        if (StringUtils.isNotBlank(entity.getStatus())) {
            wrapper.eq(Activities::getStatus, entity.getStatus());
        }
        if (entity.getDateStart() != null && entity.getDateEnd() != null) {
            wrapper.between(Activities::getDate, entity.getDateStart(), entity.getDateEnd());
        } else if (entity.getDateStart() != null) {
            wrapper.ge(Activities::getDate, entity.getDateStart());
        } else if (entity.getDateEnd() != null) {
            wrapper.le(Activities::getDate, entity.getDateEnd());
        }
        wrapper.orderByDesc(Activities::getDate).orderByDesc(Activities::getCreatedAt);

        return page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveActivity(Activities entity) {
        // 保存活动
        if (entity.getStatus() == null || entity.getStatus().isBlank()) {
            entity.setStatus("待开始");
        }
        boolean result = save(entity);
        
        if (result) {
            // 建立用户关联关系 (当前用户作为创建者)
            String userId = SecurityUtils.getUserId();
            if (userId != null) {
                ActivityUser relation = new ActivityUser();
                relation.setActivityId(entity.getId());
                relation.setUserId(userId);
                relation.setRoleType(1); // 1-创建者
                activityUserMapper.insert(relation);
            }
        }
        
        return result;
    }
}
