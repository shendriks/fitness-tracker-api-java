package dev.shendriks.fitnesstrackerapi.domain.entity;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record Activity(
    ActivityId id,
    ActivityUlid ulid,
    ActivityType activityType,
    Duration duration,
    int calories,
    Distance distance,
    Instant createdAt,
    Instant updatedAt,
    String title,
    String description,
    Instant startDate,
    List<GPSPosition> gpsPositions
) {
}
