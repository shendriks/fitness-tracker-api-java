package dev.shendriks.fitnesstrackerapi.domain.entity;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityAggregationMap;
import dev.shendriks.fitnesstrackerapi.domain.value.MilestoneId;
import dev.shendriks.fitnesstrackerapi.domain.value.MilestoneUlid;
import lombok.Getter;

@Getter
public final class Milestone extends Achievement {
    private boolean isCompleted;
    private boolean becameComplete;
    private boolean becameIncomplete;
    private int percentageCompleted;

    public Milestone(
        MilestoneId id,
        MilestoneUlid ulid,
        String name,
        String description,
        String imageFilePath,
        ActivityType activityType,
        ActivityMetric activityMetric,
        Long completionThreshold,
        boolean completed
    ) {
        super(id, ulid, name, description, imageFilePath, activityType, activityMetric, completionThreshold);
        this.isCompleted = completed;
    }

    @Override
    public MilestoneId getId() {
        return (MilestoneId) super.getId();
    }

    @Override
    public MilestoneUlid getUlid() {
        return (MilestoneUlid) super.getUlid();
    }

    public void updateCompletionPercentage(ActivityAggregationMap activityAggregationMap) {
        var activityStats = getActivityType() == null
            ? activityAggregationMap.getTotal()
            : activityAggregationMap.getByType(getActivityType());

        long value = switch (getActivityMetric()) {
            case ACTIVITY_COUNT -> activityStats.count();
            case LONGEST_SINGLE_DURATION -> activityStats.maxDuration();
            case LONGEST_SINGLE_DISTANCE -> activityStats.maxDistance();
            case TOTAL_DURATION -> activityStats.totalDuration();
            case TOTAL_DISTANCE -> activityStats.totalDistance();
        };

        boolean wasCompleted = isCompleted;
        percentageCompleted = Math.min(100, (int) ((double) (value * 100) / (double) getCompletionThreshold()));
        isCompleted = percentageCompleted >= 100;

        becameComplete = !wasCompleted && isCompleted;
        becameIncomplete = wasCompleted && !isCompleted;
    }
}
