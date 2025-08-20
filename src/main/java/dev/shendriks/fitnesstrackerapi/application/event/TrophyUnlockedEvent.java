package dev.shendriks.fitnesstrackerapi.application.event;

import dev.shendriks.fitnesstrackerapi.domain.value.TrophyId;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class TrophyUnlockedEvent extends ApplicationEvent {
    @Getter
    private final UserId userId;
    @Getter
    private final TrophyId trophyId;

    public TrophyUnlockedEvent(Object source, UserId userId, TrophyId trophyId) {
        super(source);
        this.userId = userId;
        this.trophyId = trophyId;
    }
}
