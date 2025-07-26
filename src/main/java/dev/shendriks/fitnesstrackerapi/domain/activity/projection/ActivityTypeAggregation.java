package dev.shendriks.fitnesstrackerapi.domain.activity.projection;

import dev.shendriks.fitnesstrackerapi.domain.activity.enums.ActivityType;

public interface ActivityTypeAggregation {
    ActivityType getType();

    long getCount();

    long getTotalDistance();

    long getTotalDuration();

    long getMaxDistance();

    long getMaxDuration();
}
