package com.kevin.intellieventback.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kevin.intellieventback.domin.entity.ActivityRisk;

import java.util.List;

public interface ActivityRiskService extends IService<ActivityRisk> {

    List<ActivityRisk> getRisksByActivityId(String activityId);

    boolean saveRisk(ActivityRisk risk);

    boolean updateEmergencyPlan(String riskId, String emergencyPlan);

    boolean updateRiskStatus(String riskId, String status);
}