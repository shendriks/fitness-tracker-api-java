package dev.shendriks.fitnesstrackerapi.domain.entity;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record ActivityDetails(
    ActivityId id,
    ActivityUlid ulid,
    ActivityType activityType,
    Duration duration,
    Distance distance,
    Speed averageSpeed,
    Instant createdAt,
    Instant updatedAt,
    String title,
    String description,
    Instant startDate,
    List<GPSPosition> gpsPositions,
    List<Speed> kilometerSpeeds,
    List<SpeedAtTime> speeds,
    Distance elevationGain,
    Duration motionTime,
    Duration pausingTime
) {
}
