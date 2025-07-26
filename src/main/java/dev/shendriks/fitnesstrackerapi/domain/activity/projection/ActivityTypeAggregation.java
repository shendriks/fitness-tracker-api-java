package dev.shendriks.fitnesstrackerapi.domain.activity.projection;

import dev.shendriks.fitnesstrackerapi.domain.activity.enums.ActivityType;

public interface ActivityTypeAggregation {
    ActivityType getType();

    Long getCount();

    Long getTotalDistance();

    Long getTotalDuration();

    Long getMaxDistance();

    Long getMaxDuration();
}
