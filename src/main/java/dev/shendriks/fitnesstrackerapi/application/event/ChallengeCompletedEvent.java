package dev.shendriks.fitnesstrackerapi.application.event;

import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeId;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class ChallengeCompletedEvent extends ApplicationEvent {
    @Getter
    private final UserId userId;
    @Getter
    private final ChallengeId challengeId;

    public ChallengeCompletedEvent(Object source, UserId userId, ChallengeId challengeId) {
        super(source);
        this.userId = userId;
        this.challengeId = challengeId;
    }
}
