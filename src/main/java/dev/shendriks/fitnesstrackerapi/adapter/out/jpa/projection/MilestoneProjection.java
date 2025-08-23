package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import lombok.Builder;

@Builder
public record MilestoneProjection(
    Long id,
    String ulid,
    String name,
    String description,
    String imageFilePath,
    ActivityType activityType,
    ActivityMetric activityMetric,
    Long completionThreshold,
    boolean isCompleted
) {
}
