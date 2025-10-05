package dev.shendriks.fitnesstrackerapi.application.port.in.activity;

import dev.shendriks.fitnesstrackerapi.domain.entity.ActivityDetails;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityCreationData;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

public interface CreateManualActivityUseCase {
    ActivityDetails saveManualActivityForUser(UserId userId, ActivityCreationData activityCreationData);
}
