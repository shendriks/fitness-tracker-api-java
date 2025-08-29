package dev.shendriks.fitnesstrackerapi.domain.entity;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityId;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.GPSPosition;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record Activity(
    ActivityId id,
    ActivityUlid ulid,
    ActivityType activityType,
    double duration,
    int calories,
    double distance,
    Instant createdAt,
    Instant updatedAt,
    String title,
    String description,
    Instant startDate,
    List<GPSPosition> gpsPositions
) {
}
