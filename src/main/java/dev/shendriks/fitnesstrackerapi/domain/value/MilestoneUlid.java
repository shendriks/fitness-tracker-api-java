package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public final class MilestoneUlid extends AchievementUlid {
    public MilestoneUlid(String value) {
        super(value);
    }
}
