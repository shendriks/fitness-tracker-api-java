package dev.shendriks.fitnesstrackerapi.domain.activity.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class ActivityDeletedEvent extends ApplicationEvent {
    @Getter
    private final Long activityId;

    public ActivityDeletedEvent(Object source, Long activityId) {
        super(source);
        this.activityId = activityId;
    }
}
