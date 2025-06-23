package dev.shendriks.fitnesstrackerapi.domain.activity.dto;

import dev.shendriks.fitnesstrackerapi.domain.activity.enums.ActivityType;

public record ActivityResponse(
    String id,
    String username,
    ActivityType activityType,
    int duration,
    int calories,
    String application
) {
}
