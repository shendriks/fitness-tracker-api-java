package dev.shendriks.fitnesstrackerapi.domain.activity.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class ActivityUploadedEvent extends ApplicationEvent {
    @Getter
    private final Long userId;

    public ActivityUploadedEvent(Object source, Long userId) {
        super(source);
        this.userId = userId;
    }
}
