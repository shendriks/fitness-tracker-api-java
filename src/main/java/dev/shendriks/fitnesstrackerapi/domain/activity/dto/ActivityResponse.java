package dev.shendriks.fitnesstrackerapi.domain.activity.dto;

import dev.shendriks.fitnesstrackerapi.domain.activity.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.user.dto.UserResponse;

public record ActivityResponse(
    String id,
    UserResponse user,
    ActivityType activityType,
    int duration,
    int calories,
    String application
) {
}
