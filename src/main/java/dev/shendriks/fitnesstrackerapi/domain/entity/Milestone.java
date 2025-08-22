package dev.shendriks.fitnesstrackerapi.domain.entity;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.MilestoneId;
import dev.shendriks.fitnesstrackerapi.domain.value.MilestoneUlid;
import lombok.Getter;

@Getter
public final class Milestone extends Achievement {
    private final boolean isCompleted;

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
}
