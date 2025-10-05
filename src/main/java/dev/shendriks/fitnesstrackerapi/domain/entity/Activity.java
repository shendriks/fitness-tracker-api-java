package dev.shendriks.fitnesstrackerapi.domain.entity;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import lombok.Builder;

import java.time.Instant;

/**
 * Domain aggregate representing a recorded activity without the full GPS track.
 *
 * <p>Contains summary metrics and metadata such as title, description, and timestamps.</p>
 */
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
