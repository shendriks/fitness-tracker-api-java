package dev.shendriks.fitnesstrackerapi.application.event;

import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class TrophyLostEvent extends ApplicationEvent {
    @Getter
    private final UserId userId;

    public TrophyLostEvent(Object source, UserId userId) {
        super(source);
        this.userId = userId;
    }
}
