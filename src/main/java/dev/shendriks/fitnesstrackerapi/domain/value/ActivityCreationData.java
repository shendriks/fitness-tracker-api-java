package dev.shendriks.fitnesstrackerapi.domain.value;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import lombok.Builder;

import java.time.Instant;

@Builder
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
