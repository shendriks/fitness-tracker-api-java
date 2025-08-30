package dev.shendriks.fitnesstrackerapi.application.port.in.activity;

import dev.shendriks.fitnesstrackerapi.domain.entity.ActivityDetails;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

public interface ShowActivityDetailsUseCase {
    ActivityDetails getActivityByUser(UserId userId, ActivityUlid activityId);
}
