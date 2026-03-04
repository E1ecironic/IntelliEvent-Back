package com.kevin.intellieventback.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kevin.intellieventback.domin.dto.ActivityDetailDTO;
import com.kevin.intellieventback.domin.entity.Activities;

public interface ActivitiesService extends IService<Activities> {
    IPage<Activities> pageList(Activities entity);

    boolean saveActivity(Activities entity);

    Activities getActivityDetail(String id);

    ActivityDetailDTO getActivityFullDetail(String id);
}
