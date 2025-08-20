package dev.shendriks.fitnesstrackerapi.application.port.in.activity;

import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

public interface ShowActivityDetailsUseCase {
    Activity getActivityByUser(UserId userId, ActivityUlid activityId);
}
