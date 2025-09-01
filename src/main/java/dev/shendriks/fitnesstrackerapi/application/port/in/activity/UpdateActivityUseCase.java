package dev.shendriks.fitnesstrackerapi.application.port.in.activity;

import dev.shendriks.fitnesstrackerapi.domain.entity.ActivityDetails;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityUpdateData;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

public interface UpdateActivityUseCase {
    ActivityDetails updateActivityForUser(UserId userId, ActivityUlid activityUlid, ActivityUpdateData activityUpdateData);
}

