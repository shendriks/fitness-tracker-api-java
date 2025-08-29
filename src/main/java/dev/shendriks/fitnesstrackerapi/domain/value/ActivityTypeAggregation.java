package dev.shendriks.fitnesstrackerapi.domain.value;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import lombok.Builder;

@Builder
public record ActivityTypeAggregation(
    ActivityType type,

    long count,

    Distance totalDistance,

    Duration totalDuration,

    Distance maxDistance,

    Duration maxDuration
) {
}
