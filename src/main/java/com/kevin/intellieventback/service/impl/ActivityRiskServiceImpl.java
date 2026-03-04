package com.kevin.intellieventback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kevin.intellieventback.domin.entity.ActivityRisk;
import com.kevin.intellieventback.mapper.ActivityRiskMapper;
import com.kevin.intellieventback.service.ActivityRiskService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityRiskServiceImpl extends ServiceImpl<ActivityRiskMapper, ActivityRisk> implements ActivityRiskService {

    @Override
    public List<ActivityRisk> getRisksByActivityId(String activityId) {
        LambdaQueryWrapper<ActivityRisk> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityRisk::getActivityId, activityId)
               .orderByDesc(ActivityRisk::getLevel)
               .orderByDesc(ActivityRisk::getCreatedAt);
        return list(wrapper);
    }

    @Override
    public boolean saveRisk(ActivityRisk risk) {
        if (StringUtils.isBlank(risk.getStatus())) {
            risk.setStatus("未处理");
        }
        return save(risk);
    }

    @Override
    public boolean updateEmergencyPlan(String riskId, String emergencyPlan) {
        ActivityRisk risk = getById(riskId);
        if (risk == null) {
            return false;
        }
        risk.setEmergencyPlan(emergencyPlan);
        return updateById(risk);
    }

    @Override
    public boolean updateRiskStatus(String riskId, String status) {
        ActivityRisk risk = getById(riskId);
        if (risk == null) {
            return false;
        }
        risk.setStatus(status);
        return updateById(risk);
    }
}