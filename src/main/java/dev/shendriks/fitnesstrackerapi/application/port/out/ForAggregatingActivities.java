package dev.shendriks.fitnesstrackerapi.application.port.out;

import dev.shendriks.fitnesstrackerapi.domain.value.ActivityAggregationMap;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

import java.time.Instant;

public interface ForAggregatingActivities {
    ActivityAggregationMap aggregateForUserByTypeInTimeRange(UserId userId, Instant from, Instant to);

    ActivityAggregationMap aggregateForUserByType(UserId userId);
}
