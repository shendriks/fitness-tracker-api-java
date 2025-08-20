package dev.shendriks.fitnesstrackerapi.application.port.in.activity;

import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityUpdateData;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

public interface UpdateActivityUseCase {
    Activity updateActivityForUser(UserId userId, ActivityUlid activityUlid, ActivityUpdateData activityUpdateData);
}

