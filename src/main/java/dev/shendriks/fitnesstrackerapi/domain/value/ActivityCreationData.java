package dev.shendriks.fitnesstrackerapi.domain.value;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;

import java.time.Instant;

public record ActivityCreationData(
    ActivityType activityType,
    int duration,
    int calories,
    String title,
    String description,
    int distance,
    Instant startDate
) {
}
