package dev.shendriks.fitnesstrackerapi.domain.value;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;

public record AchievementCompletionRequest(
    ActivityAggregationMap activityAggregationMap,
    ActivityType activityType,
    ActivityMetric activityMetric,
    int currentPercentageCompleted,
    Long completionThreshold
) {
}
