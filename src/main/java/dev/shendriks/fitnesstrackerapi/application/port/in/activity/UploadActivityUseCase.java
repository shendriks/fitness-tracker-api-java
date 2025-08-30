package dev.shendriks.fitnesstrackerapi.application.port.in.activity;

import dev.shendriks.fitnesstrackerapi.domain.entity.ActivityDetails;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityUploadData;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

public interface UploadActivityUseCase {
    ActivityDetails uploadActivityForUser(UserId userId, ActivityUploadData activityUploadData);
}
