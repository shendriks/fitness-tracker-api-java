package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.Getter;

@Getter
public final class ChallengeUlid extends AchievementUlid {
    public ChallengeUlid(String value) {
        super(value);
    }
}
