package dev.shendriks.fitnesstrackerapi.application.port.in.activity;

import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityCreationData;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

public interface CreateActivityUseCase {
    Activity saveActivityForUser(UserId userId, ActivityCreationData activityCreationData);
}
