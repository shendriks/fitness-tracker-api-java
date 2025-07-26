package dev.shendriks.fitnesstrackerapi.domain.challenge.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class ChallengeJoinedEvent extends ApplicationEvent {
    @Getter
    private final Long userId;
    @Getter
    private final Long challengeId;

    public ChallengeJoinedEvent(Object source, Long userId, Long challengeId) {
        super(source);
        this.userId = userId;
        this.challengeId = challengeId;
    }
}
