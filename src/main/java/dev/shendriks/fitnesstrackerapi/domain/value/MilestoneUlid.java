package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.Getter;

@Getter
public final class MilestoneUlid extends AchievementUlid {
    public MilestoneUlid(String value) {
        super(value);
    }
}
