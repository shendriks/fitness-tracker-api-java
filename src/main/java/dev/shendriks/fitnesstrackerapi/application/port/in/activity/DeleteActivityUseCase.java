package dev.shendriks.fitnesstrackerapi.application.port.in.activity;

import dev.shendriks.fitnesstrackerapi.domain.value.ActivityUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

public interface DeleteActivityUseCase {
    void deleteActivityForUser(UserId userId, ActivityUlid activityId);
}
