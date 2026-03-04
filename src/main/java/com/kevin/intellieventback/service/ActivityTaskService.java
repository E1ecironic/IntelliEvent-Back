package com.kevin.intellieventback.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kevin.intellieventback.domin.entity.ActivityTask;

import java.util.List;

public interface ActivityTaskService extends IService<ActivityTask> {

    List<ActivityTask> getTasksByActivityId(String activityId);

    boolean saveTask(ActivityTask task);

    boolean updateTaskStatus(String taskId, String status);
}