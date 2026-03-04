package com.kevin.intellieventback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kevin.intellieventback.domin.entity.ActivitySupplier;
import com.kevin.intellieventback.mapper.ActivitySupplierMapper;
import com.kevin.intellieventback.service.ActivitySupplierService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivitySupplierServiceImpl extends ServiceImpl<ActivitySupplierMapper, ActivitySupplier> implements ActivitySupplierService {

    @Override
    public List<ActivitySupplier> getSuppliersByActivityId(String activityId) {
        LambdaQueryWrapper<ActivitySupplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySupplier::getActivityId, activityId)
               .orderByDesc(ActivitySupplier::getCreatedAt);
        return list(wrapper);
    }
}