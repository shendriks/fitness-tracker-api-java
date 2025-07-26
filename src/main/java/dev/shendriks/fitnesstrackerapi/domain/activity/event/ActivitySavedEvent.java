package dev.shendriks.fitnesstrackerapi.domain.activity.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class ActivitySavedEvent extends ApplicationEvent {
    @Getter
    private final Long activityId;

    public ActivitySavedEvent(Object source, Long activityId) {
        super(source);
        this.activityId = activityId;
    }
}
