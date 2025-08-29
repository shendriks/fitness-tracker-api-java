package dev.shendriks.fitnesstrackerapi.domain.entity;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.*;

import java.time.Instant;
import java.util.List;

public record ActivityDetails(
    ActivityId id,
    ActivityUlid ulid,
    ActivityType activityType,
    Duration duration,
    Distance distance,
    Instant createdAt,
    Instant updatedAt,
    String title,
    String description,
    Instant startDate,
    List<GPSPosition> gpsPositions
) {
}
