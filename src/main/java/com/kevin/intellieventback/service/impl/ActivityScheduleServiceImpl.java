package com.kevin.intellieventback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kevin.intellieventback.domin.entity.ActivitySchedule;
import com.kevin.intellieventback.domin.entity.Users;
import com.kevin.intellieventback.mapper.ActivityScheduleMapper;
import com.kevin.intellieventback.mapper.UsersMapper;
import com.kevin.intellieventback.service.ActivityScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ActivityScheduleServiceImpl extends ServiceImpl<ActivityScheduleMapper, ActivitySchedule> implements ActivityScheduleService {

    @Autowired
    private UsersMapper usersMapper;

    @Override
    public List<ActivitySchedule> getSchedulesByActivityId(String activityId) {
        LambdaQueryWrapper<ActivitySchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySchedule::getActivityId, activityId)
               .orderByAsc(ActivitySchedule::getScheduleDate)
               .orderByAsc(ActivitySchedule::getSortOrder)
               .orderByAsc(ActivitySchedule::getStartTime);
        List<ActivitySchedule> schedules = list(wrapper);

        // 填充负责人姓名
        if (!schedules.isEmpty()) {
            List<String> responsibleIds = schedules.stream()
                    .map(ActivitySchedule::getResponsible)
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .collect(Collectors.toList());

            if (!responsibleIds.isEmpty()) {
                List<Users> users = usersMapper.selectBatchIds(responsibleIds);
                Map<String, String> userNameMap = users.stream()
                        .collect(Collectors.toMap(Users::getId, u -> {
                            if (StringUtils.isNotBlank(u.getRealName())) {
                                return u.getRealName();
                            } else if (StringUtils.isNotBlank(u.getUserName())) {
                                return u.getUserName();
                            }
                            return u.getId();
                        }, (v1, v2) -> v1));

                for (ActivitySchedule schedule : schedules) {
                    if (StringUtils.isNotBlank(schedule.getResponsible())) {
                        schedule.setResponsibleName(userNameMap.get(schedule.getResponsible()));
                    }
                }
            }
        }

        return schedules;
    }

    @Override
    public boolean saveSchedule(ActivitySchedule schedule) {
        if (schedule.getSortOrder() == null) {
            // 自动计算排序号
            LambdaQueryWrapper<ActivitySchedule> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ActivitySchedule::getActivityId, schedule.getActivityId());
            long count = count(wrapper);
            schedule.setSortOrder((int) count + 1);
        }
        return save(schedule);
    }

    @Override
    public boolean updateSortOrder(String scheduleId, Integer sortOrder) {
        ActivitySchedule schedule = getById(scheduleId);
        if (schedule == null) {
            return false;
        }
        schedule.setSortOrder(sortOrder);
        return updateById(schedule);
    }
}