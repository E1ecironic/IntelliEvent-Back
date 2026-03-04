package com.kevin.intellieventback.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kevin.intellieventback.domin.entity.ActivityParticipant;

import java.util.List;

public interface ActivityParticipantService extends IService<ActivityParticipant> {

    List<ActivityParticipant> getParticipantsByActivityId(String activityId);

    boolean saveParticipant(ActivityParticipant participant);

    boolean updateParticipantStatus(String participantId, String status);
}