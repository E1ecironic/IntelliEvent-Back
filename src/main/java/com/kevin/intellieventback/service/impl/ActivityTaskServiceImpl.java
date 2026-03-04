package com.kevin.intellieventback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kevin.intellieventback.domin.entity.ActivityTask;
import com.kevin.intellieventback.domin.entity.Users;
import com.kevin.intellieventback.mapper.ActivityTaskMapper;
import com.kevin.intellieventback.mapper.UsersMapper;
import com.kevin.intellieventback.service.ActivityTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ActivityTaskServiceImpl extends ServiceImpl<ActivityTaskMapper, ActivityTask> implements ActivityTaskService {

    @Autowired
    private UsersMapper usersMapper;

    @Override
    public List<ActivityTask> getTasksByActivityId(String activityId) {
        LambdaQueryWrapper<ActivityTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityTask::getActivityId, activityId)
               .orderByDesc(ActivityTask::getCreatedAt);
        List<ActivityTask> tasks = list(wrapper);

        // 填充负责人姓名
        if (!tasks.isEmpty()) {
            List<String> responsibleIds = tasks.stream()
                    .map(ActivityTask::getResponsible)
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

                for (ActivityTask task : tasks) {
                    if (StringUtils.isNotBlank(task.getResponsible())) {
                        task.setResponsibleName(userNameMap.get(task.getResponsible()));
                    }
                }
            }
        }

        return tasks;
    }

    @Override
    public boolean saveTask(ActivityTask task) {
        if (StringUtils.isBlank(task.getStatus())) {
            task.setStatus("未开始");
        }
        if (StringUtils.isBlank(task.getPriority())) {
            task.setPriority("中");
        }
        return save(task);
    }

    @Override
    public boolean updateTaskStatus(String taskId, String status) {
        ActivityTask task = getById(taskId);
        if (task == null) {
            return false;
        }
        task.setStatus(status);
        return updateById(task);
    }
}