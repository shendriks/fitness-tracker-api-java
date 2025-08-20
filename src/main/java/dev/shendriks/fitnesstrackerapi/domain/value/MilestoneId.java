package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.Getter;

@Getter
public final class MilestoneId extends AchievementId {
    public MilestoneId(Long value) {
        super(value);
    }
}
