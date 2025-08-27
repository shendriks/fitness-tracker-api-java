package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public final class ChallengeUlid extends AchievementUlid {
    public ChallengeUlid(String value) {
        super(value);
    }
}
