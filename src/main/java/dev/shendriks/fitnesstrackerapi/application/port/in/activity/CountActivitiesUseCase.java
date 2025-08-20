package dev.shendriks.fitnesstrackerapi.application.port.in.activity;

import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

public interface CountActivitiesUseCase {
    long getActivityCountByUser(UserId userId);
}
