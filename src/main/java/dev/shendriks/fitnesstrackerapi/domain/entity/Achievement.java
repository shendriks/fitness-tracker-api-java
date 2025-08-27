package dev.shendriks.fitnesstrackerapi.domain.entity;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityMetric;
import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.AchievementId;
import dev.shendriks.fitnesstrackerapi.domain.value.AchievementUlid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
public class Achievement {
    private AchievementId id;
    private AchievementUlid ulid;
    private String name;
    private String description;
    private String imageFilePath;
    private ActivityType activityType;
    private ActivityMetric activityMetric;
    private Long completionThreshold;
}
