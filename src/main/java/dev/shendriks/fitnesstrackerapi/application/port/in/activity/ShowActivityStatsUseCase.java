package dev.shendriks.fitnesstrackerapi.application.port.in.activity;

import dev.shendriks.fitnesstrackerapi.domain.value.ActivityAggregationMap;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

import java.time.Instant;

public interface ShowActivityStatsUseCase {
    ActivityAggregationMap getActivityStatsByUser(UserId id, Instant start, Instant end);
}
