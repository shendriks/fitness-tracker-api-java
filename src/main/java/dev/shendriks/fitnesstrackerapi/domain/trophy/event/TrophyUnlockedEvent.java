package dev.shendriks.fitnesstrackerapi.domain.trophy.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class TrophyUnlockedEvent extends ApplicationEvent {
    @Getter
    private final Long trophyId;

    public TrophyUnlockedEvent(Object source, Long trophyId) {
        super(source);
        this.trophyId = trophyId;
    }
}
