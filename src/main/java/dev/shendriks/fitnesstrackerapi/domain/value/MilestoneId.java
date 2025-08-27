package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public final class MilestoneId extends AchievementId {
    public MilestoneId(Long value) {
        super(value);
    }
}
