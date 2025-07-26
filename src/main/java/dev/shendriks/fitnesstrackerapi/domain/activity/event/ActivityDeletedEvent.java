package dev.shendriks.fitnesstrackerapi.domain.activity.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class ActivityDeletedEvent extends ApplicationEvent {
    @Getter
    private final Long userId;

    public ActivityDeletedEvent(Object source, Long userId) {
        super(source);
        this.userId = userId;
    }
}
