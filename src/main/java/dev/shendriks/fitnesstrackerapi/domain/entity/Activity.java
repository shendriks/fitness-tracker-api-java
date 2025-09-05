package dev.shendriks.fitnesstrackerapi.domain.entity;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import lombok.Builder;

import java.time.Instant;

@Builder
public record Activity(
    ActivityId id,
    ActivityUlid ulid,
    ActivityType activityType,
    Duration duration,
    Distance distance,
    String title,
    String description,
    Instant startDate,
    Instant createdAt,
    Instant updatedAt,
    Speed averageSpeed,
    ImageData trackPreviewImage
) {
}
