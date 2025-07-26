package dev.shendriks.fitnesstrackerapi.domain.trophy.event;

import org.springframework.context.ApplicationEvent;

public class TrophyLostEvent extends ApplicationEvent {
    private final Long trophyId;

    public TrophyLostEvent(Object source, Long trophyId) {
        super(source);
        this.trophyId = trophyId;
    }
}
