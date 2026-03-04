package com.kevin.intellieventback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kevin.intellieventback.domin.entity.ActivityParticipant;
import com.kevin.intellieventback.mapper.ActivityParticipantMapper;
import com.kevin.intellieventback.service.ActivityParticipantService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityParticipantServiceImpl extends ServiceImpl<ActivityParticipantMapper, ActivityParticipant> implements ActivityParticipantService {

    @Override
    public List<ActivityParticipant> getParticipantsByActivityId(String activityId) {
        LambdaQueryWrapper<ActivityParticipant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityParticipant::getActivityId, activityId)
               .orderByDesc(ActivityParticipant::getCreatedAt);
        return list(wrapper);
    }

    @Override
    public boolean saveParticipant(ActivityParticipant participant) {
        if (StringUtils.isBlank(participant.getStatus())) {
            participant.setStatus("待确认");
        }
        return save(participant);
    }

    @Override
    public boolean updateParticipantStatus(String participantId, String status) {
        ActivityParticipant participant = getById(participantId);
        if (participant == null) {
            return false;
        }
        participant.setStatus(status);
        return updateById(participant);
    }
}