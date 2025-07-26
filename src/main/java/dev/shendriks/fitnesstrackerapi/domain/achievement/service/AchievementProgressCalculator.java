package dev.shendriks.fitnesstrackerapi.domain.achievement.service;

import dev.shendriks.fitnesstrackerapi.domain.achievement.entity.Achievement;
import dev.shendriks.fitnesstrackerapi.domain.activity.projection.ActivityAggregation;
import org.springframework.stereotype.Component;

@Component
public class AchievementProgressCalculator {
    public int calculateCompletionPercentage(Achievement achievement, ActivityAggregation activityStats) {
        long value = switch (achievement.getActivityMetric()) {
            case ACTIVITY_COUNT -> activityStats.getCount();
            case LONGEST_SINGLE_DURATION -> activityStats.getMaxDuration();
            case LONGEST_SINGLE_DISTANCE -> activityStats.getMaxDistance();
            case TOTAL_DURATION -> activityStats.getTotalDuration();
            case TOTAL_DISTANCE -> activityStats.getTotalDistance();
        };

        return Math.min(100, (int) ((double) (value * 100) / (double) achievement.getCompletionThreshold()));
    }
}
