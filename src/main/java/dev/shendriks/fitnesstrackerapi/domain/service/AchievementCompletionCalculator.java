package dev.shendriks.fitnesstrackerapi.domain.service;

import dev.shendriks.fitnesstrackerapi.domain.value.AchievementCompletionRequest;
import dev.shendriks.fitnesstrackerapi.domain.value.AchievementCompletionResult;
import org.springframework.stereotype.Service;

@Service
public class AchievementCompletionCalculator {
    public AchievementCompletionResult calculateAchievementCompletion(AchievementCompletionRequest request) {
        var activityStats = request.activityType() == null
            ? request.activityAggregationMap().getTotal()
            : request.activityAggregationMap().getByType(request.activityType());

        double value = switch (request.activityMetric()) {
            case ACTIVITY_COUNT -> activityStats.count();
            case LONGEST_SINGLE_DURATION -> activityStats.maxDuration().toSeconds();
            case LONGEST_SINGLE_DISTANCE -> activityStats.maxDistance().toMeters();
            case TOTAL_DURATION -> activityStats.totalDuration().toSeconds();
            case TOTAL_DISTANCE -> activityStats.totalDistance().toMeters();
        };

        boolean wasCompleted = request.currentPercentageCompleted() >= 100;
        int percentageCompleted = Math.min(100, (int) ((value * 100) / (double) request.completionThreshold()));
        boolean isCompleted = percentageCompleted >= 100;

        boolean becameComplete = !wasCompleted && isCompleted;
        boolean becameIncomplete = wasCompleted && !isCompleted;

        return new AchievementCompletionResult(
            percentageCompleted,
            becameComplete,
            becameIncomplete
        );
    }
}
