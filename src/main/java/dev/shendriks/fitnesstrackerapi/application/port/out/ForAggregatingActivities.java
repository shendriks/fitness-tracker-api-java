package dev.shendriks.fitnesstrackerapi.application.port.out;

import dev.shendriks.fitnesstrackerapi.domain.value.ActivityAggregationMap;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

import java.time.Instant;

public interface ForAggregatingActivities {
    ActivityAggregationMap aggregateForUserInTimeRange(UserId userId, Instant from, Instant to);

    ActivityAggregationMap aggregateForUser(UserId userId);
}
