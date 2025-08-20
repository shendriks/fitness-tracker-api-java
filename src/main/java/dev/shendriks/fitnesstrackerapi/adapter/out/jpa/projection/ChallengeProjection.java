package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;

import java.time.Instant;

public interface ChallengeProjection {
    Long getId();

    String getUlid();

    String getName();

    String getDescription();

    String getImageFilePath();

    ActivityType getActivityType();

    ActivityMetric getActivityMetric();

    Long getCompletionThreshold();

    Instant getStartDate();

    Instant getEndDate();

    boolean isHasUserJoined();
}
