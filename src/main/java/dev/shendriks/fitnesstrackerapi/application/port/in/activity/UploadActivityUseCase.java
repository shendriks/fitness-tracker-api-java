package dev.shendriks.fitnesstrackerapi.application.port.in.activity;

import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityUploadData;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

public interface UploadActivityUseCase {
    Activity uploadActivityForUser(UserId userId, ActivityUploadData activityUploadData);
}
