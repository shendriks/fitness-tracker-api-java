package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ChallengeId extends AchievementId {
    public ChallengeId(Long value) {
        super(value);
    }
}
