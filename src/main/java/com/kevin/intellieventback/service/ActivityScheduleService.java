package com.kevin.intellieventback.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kevin.intellieventback.domin.entity.ActivitySchedule;

import java.util.List;

public interface ActivityScheduleService extends IService<ActivitySchedule> {

    List<ActivitySchedule> getSchedulesByActivityId(String activityId);

    boolean saveSchedule(ActivitySchedule schedule);

    boolean updateSortOrder(String scheduleId, Integer sortOrder);
}