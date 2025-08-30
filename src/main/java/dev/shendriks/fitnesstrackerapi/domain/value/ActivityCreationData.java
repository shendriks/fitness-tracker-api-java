package dev.shendriks.fitnesstrackerapi.domain.value;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ActivityCreationData(
    ActivityType activityType,
    Duration duration,
    Distance distance,
    @Deprecated
    int calories,
    String title,
    String description,
    Instant startDate
) {
}
