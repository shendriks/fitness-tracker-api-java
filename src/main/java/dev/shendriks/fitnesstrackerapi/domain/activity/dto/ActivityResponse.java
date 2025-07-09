package dev.shendriks.fitnesstrackerapi.domain.activity.dto;

import dev.shendriks.fitnesstrackerapi.domain.activity.enums.ActivityType;

import java.time.Instant;
import java.util.List;

public record ActivityResponse(
    String id,
    ActivityType activityType,
    int duration,
    int calories,
    Instant createdAt,
    Instant updatedAt,
    List<GPSPositionResponse> gpsPositions
) {
}
