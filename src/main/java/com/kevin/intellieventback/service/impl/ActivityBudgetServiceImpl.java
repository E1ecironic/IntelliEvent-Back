package com.kevin.intellieventback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kevin.intellieventback.domin.entity.ActivityBudget;
import com.kevin.intellieventback.domin.entity.ActivitySupplier;
import com.kevin.intellieventback.mapper.ActivityBudgetMapper;
import com.kevin.intellieventback.mapper.ActivitySupplierMapper;
import com.kevin.intellieventback.service.ActivityBudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ActivityBudgetServiceImpl extends ServiceImpl<ActivityBudgetMapper, ActivityBudget> implements ActivityBudgetService {

    @Autowired
    private ActivitySupplierMapper supplierMapper;

    @Override
    public List<ActivityBudget> getBudgetsByActivityId(String activityId) {
        LambdaQueryWrapper<ActivityBudget> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityBudget::getActivityId, activityId)
               .orderByDesc(ActivityBudget::getCreatedAt);
        List<ActivityBudget> budgets = list(wrapper);

        // 填充供应商名称
        if (!budgets.isEmpty()) {
            List<String> supplierIds = budgets.stream()
                    .map(ActivityBudget::getSupplierId)
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .collect(Collectors.toList());

            if (!supplierIds.isEmpty()) {
                List<ActivitySupplier> suppliers = supplierMapper.selectBatchIds(supplierIds);
                Map<String, String> supplierNameMap = suppliers.stream()
                        .collect(Collectors.toMap(ActivitySupplier::getId, ActivitySupplier::getName, (v1, v2) -> v1));

                for (ActivityBudget budget : budgets) {
                    if (StringUtils.isNotBlank(budget.getSupplierId())) {
                        budget.setSupplierName(supplierNameMap.get(budget.getSupplierId()));
                    }
                }
            }
        }

        return budgets;
    }

    @Override
    public boolean saveBudget(ActivityBudget budget) {
        if (StringUtils.isBlank(budget.getStatus())) {
            budget.setStatus("待确认");
        }
        // 自动计算小计
        if (budget.getQuantity() != null && budget.getUnitPrice() != null) {
            budget.setTotal(budget.getUnitPrice().multiply(new BigDecimal(budget.getQuantity())));
        }
        return save(budget);
    }

    @Override
    public BigDecimal getTotalBudgetByActivityId(String activityId) {
        LambdaQueryWrapper<ActivityBudget> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityBudget::getActivityId, activityId)
               .eq(ActivityBudget::getStatus, "已确认");
        List<ActivityBudget> budgets = list(wrapper);
        return budgets.stream()
                .map(ActivityBudget::getTotal)
                .filter(total -> total != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<String, Object> getBudgetStatistics(String activityId) {
        Map<String, Object> statistics = new HashMap<>();
        List<ActivityBudget> budgets = getBudgetsByActivityId(activityId);

        BigDecimal totalAllocated = budgets.stream()
                .map(ActivityBudget::getTotal)
                .filter(total -> total != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal confirmedAmount = budgets.stream()
                .filter(b -> "已确认".equals(b.getStatus()))
                .map(ActivityBudget::getTotal)
                .filter(total -> total != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> categoryStats = budgets.stream()
                .collect(Collectors.groupingBy(
                        ActivityBudget::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, ActivityBudget::getTotal, BigDecimal::add)
                ));

        statistics.put("totalAllocated", totalAllocated);
        statistics.put("confirmedAmount", confirmedAmount);
        statistics.put("categoryStats", categoryStats);
        statistics.put("itemCount", budgets.size());

        return statistics;
    }
}