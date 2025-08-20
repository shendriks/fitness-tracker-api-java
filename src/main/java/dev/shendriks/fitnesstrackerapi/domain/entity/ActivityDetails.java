package dev.shendriks.fitnesstrackerapi.domain.entity;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityId;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.GPSPosition;

import java.time.Instant;
import java.util.List;

public record ActivityDetails(
    ActivityId id,
    ActivityUlid ulid,
    ActivityType activityType,
    int duration,
    int calories,
    Instant createdAt,
    Instant updatedAt,
    String title,
    String description,
    int distance,
    Instant startDate,
    List<GPSPosition> gpsPositions
) {
}
