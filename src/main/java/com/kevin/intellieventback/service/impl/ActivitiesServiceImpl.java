package com.kevin.intellieventback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kevin.basecore.modules.security.utils.SecurityUtils;
import com.kevin.intellieventback.domin.dto.ActivityDetailDTO;
import com.kevin.intellieventback.domin.entity.Activities;
import com.kevin.intellieventback.domin.entity.ActivityUser;
import com.kevin.intellieventback.domin.entity.Users;
import com.kevin.intellieventback.mapper.ActivitiesMapper;
import com.kevin.intellieventback.mapper.ActivityUserMapper;
import com.kevin.intellieventback.mapper.UsersMapper;
import com.kevin.intellieventback.service.ActivitiesService;
import com.kevin.intellieventback.service.ActivityBudgetService;
import com.kevin.intellieventback.service.ActivityParticipantService;
import com.kevin.intellieventback.service.ActivityRiskService;
import com.kevin.intellieventback.service.ActivityScheduleService;
import com.kevin.intellieventback.service.ActivitySupplierService;
import com.kevin.intellieventback.service.ActivityTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivitiesServiceImpl extends ServiceImpl<ActivitiesMapper, Activities> implements ActivitiesService {

    private static final Logger logger = LoggerFactory.getLogger(ActivitiesServiceImpl.class);

    @Autowired
    private ActivityUserMapper activityUserMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private ActivityTaskService activityTaskService;

    @Autowired
    private ActivityBudgetService activityBudgetService;

    @Autowired
    private ActivityParticipantService activityParticipantService;

    @Autowired
    private ActivitySupplierService activitySupplierService;

    @Autowired
    private ActivityRiskService activityRiskService;

    @Autowired
    private ActivityScheduleService activityScheduleService;

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

        IPage<Activities> resultPage = page(page, wrapper);

        // 填充负责人姓名
        List<Activities> records = resultPage.getRecords();
        if (!records.isEmpty()) {
            // 收集所有负责人ID
            List<String> responsibleIds = records.stream()
                    .map(Activities::getResponsible)
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .collect(Collectors.toList());

            if (!responsibleIds.isEmpty()) {
                // 查询用户信息
                List<Users> users = usersMapper.selectBatchIds(responsibleIds);
                java.util.Map<String, String> userNameMap = users.stream()
                        .collect(Collectors.toMap(Users::getId, u -> {
                            if (StringUtils.isNotBlank(u.getRealName())) {
                                return u.getRealName();
                            } else if (StringUtils.isNotBlank(u.getUserName())) {
                                return u.getUserName();
                            }
                            return u.getId();
                        }, (v1, v2) -> v1));

                // 填充负责人姓名
                for (Activities activity : records) {
                    if (StringUtils.isNotBlank(activity.getResponsible())) {
                        String userName = userNameMap.get(activity.getResponsible());
                        if (StringUtils.isNotBlank(userName)) {
                            activity.setResponsibleName(userName);
                        }
                    }
                }
            }
        }

        return resultPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveActivity(Activities entity) {
        logger.debug("开始保存活动，entity: {}", entity);
        
        if (entity.getStatus() == null || entity.getStatus().isBlank()) {
            entity.setStatus("待开始");
        }
        boolean result = save(entity);
        logger.debug("活动保存结果: {}, 活动ID: {}", result, entity.getId());
        
        if (result && entity.getId() != null) {
            String currentUserId = SecurityUtils.getUserId();
            logger.debug("当前用户ID: {}", currentUserId);
            
            if (currentUserId != null) {
                ActivityUser creatorRelation = new ActivityUser();
                creatorRelation.setActivityId(entity.getId());
                creatorRelation.setUserId(currentUserId);
                creatorRelation.setRoleType(1);
                logger.debug("准备插入 activity_user (创建者): {}", creatorRelation);
                activityUserMapper.insert(creatorRelation);
                logger.debug("activity_user (创建者) 插入成功");
            } else {
                logger.warn("用户ID为空，无法创建 activity_user 关联记录 (创建者)");
            }
            
            if (StringUtils.isNotBlank(entity.getResponsible())) {
                if (!currentUserId.equals(entity.getResponsible())) {
                    ActivityUser responsibleRelation = new ActivityUser();
                    responsibleRelation.setActivityId(entity.getId());
                    responsibleRelation.setUserId(entity.getResponsible());
                    responsibleRelation.setRoleType(3);
                    logger.debug("准备插入 activity_user (负责人): {}", responsibleRelation);
                    activityUserMapper.insert(responsibleRelation);
                    logger.debug("activity_user (负责人) 插入成功");
                }
            }
        }
        
        return result;
    }

    @Override
    public Activities getActivityDetail(String id) {
        Activities activity = getById(id);
        if (activity == null) {
            return null;
        }

        if (StringUtils.isNotBlank(activity.getResponsible())) {
            Users user = usersMapper.selectById(activity.getResponsible());
            if (user != null) {
                String userName = StringUtils.isNotBlank(user.getRealName()) ? user.getRealName() :
                               StringUtils.isNotBlank(user.getUserName()) ? user.getUserName() : user.getId();
                activity.setResponsibleName(userName);
            }
        }

        return activity;
    }

    @Override
    public ActivityDetailDTO getActivityFullDetail(String id) {
        ActivityDetailDTO dto = new ActivityDetailDTO();

        // 1. 获取活动基本信息
        Activities activity = getActivityDetail(id);
        dto.setActivity(activity);

        if (activity == null) {
            return dto;
        }

        // 2. 获取日程列表
        dto.setSchedules(activityScheduleService.getSchedulesByActivityId(id));

        // 3. 获取任务列表
        dto.setTasks(activityTaskService.getTasksByActivityId(id));

        // 4. 获取预算列表
        dto.setBudgets(activityBudgetService.getBudgetsByActivityId(id));

        // 5. 获取人员列表
        dto.setParticipants(activityParticipantService.getParticipantsByActivityId(id));

        // 6. 获取供应商列表
        dto.setSuppliers(activitySupplierService.getSuppliersByActivityId(id));

        // 7. 获取风险列表
        dto.setRisks(activityRiskService.getRisksByActivityId(id));

        return dto;
    }
}
