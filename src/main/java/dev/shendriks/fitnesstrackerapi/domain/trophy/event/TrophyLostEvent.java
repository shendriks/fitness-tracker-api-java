package dev.shendriks.fitnesstrackerapi.domain.trophy.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class TrophyLostEvent extends ApplicationEvent {
    @Getter
    private final Long userId;
    @Getter
    private final Long trophyId;

    public TrophyLostEvent(Object source, Long userId, Long trophyId) {
        super(source);
        this.userId = userId;
        this.trophyId = trophyId;
    }
}
