package dev.shendriks.fitnesstrackerapi.adapter.out.jpa.projection;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;

public interface ActivityTypeAggregationDbProjection {
    ActivityType getType();

    long getCount();

    long getTotalDistance();

    long getTotalDuration();

    long getMaxDistance();

    long getMaxDuration();
}
