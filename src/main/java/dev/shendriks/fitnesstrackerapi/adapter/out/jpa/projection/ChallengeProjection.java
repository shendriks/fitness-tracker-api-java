package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ChallengeProjection(
    Long id,
    String ulid,
    String name,
    String description,
    String imageFilePath,
    ActivityType activityType,
    ActivityMetric activityMetric,
    Long completionThreshold,
    Instant startDate,
    Instant endDate,
    boolean hasUserJoined
) {
}
