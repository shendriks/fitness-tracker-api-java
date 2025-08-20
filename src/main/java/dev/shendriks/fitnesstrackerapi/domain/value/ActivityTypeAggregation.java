package dev.shendriks.fitnesstrackerapi.domain.value;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;

public record ActivityTypeAggregation(
    ActivityType type,

    long count,

    long totalDistance,

    long totalDuration,

    long maxDistance,

    long maxDuration
) {
}
