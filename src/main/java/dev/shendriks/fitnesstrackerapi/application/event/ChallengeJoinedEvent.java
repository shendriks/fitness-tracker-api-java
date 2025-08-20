package dev.shendriks.fitnesstrackerapi.application.event;

import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class ChallengeJoinedEvent extends ApplicationEvent {
    @Getter
    private final UserId userId;
    @Getter
    private final ChallengeUlid challengeUlid;

    public ChallengeJoinedEvent(Object source, UserId userId, ChallengeUlid challengeUlid) {
        super(source);
        this.userId = userId;
        this.challengeUlid = challengeUlid;
    }
}
