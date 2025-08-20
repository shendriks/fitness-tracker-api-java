package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;

public interface MilestoneProjection {
    String getId();

    String getUlid();

    String getName();

    String getDescription();

    String getImageFilePath();

    ActivityType getActivityType();

    ActivityMetric getActivityMetric();

    Long getCompletionThreshold();

    boolean isCompleted();
}
