package com.kevin.intellieventback.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kevin.intellieventback.domin.entity.ActivitySupplier;

import java.util.List;

public interface ActivitySupplierService extends IService<ActivitySupplier> {

    List<ActivitySupplier> getSuppliersByActivityId(String activityId);
}