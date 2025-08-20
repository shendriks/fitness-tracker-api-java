package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.Getter;

@Getter
public class ChallengeId extends AchievementId {
    public ChallengeId(Long value) {
        super(value);
    }
}
