package com.kevin.intellieventback.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kevin.intellieventback.domin.entity.ActivityBudget;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ActivityBudgetService extends IService<ActivityBudget> {

    List<ActivityBudget> getBudgetsByActivityId(String activityId);

    boolean saveBudget(ActivityBudget budget);

    BigDecimal getTotalBudgetByActivityId(String activityId);

    Map<String, Object> getBudgetStatistics(String activityId);
}