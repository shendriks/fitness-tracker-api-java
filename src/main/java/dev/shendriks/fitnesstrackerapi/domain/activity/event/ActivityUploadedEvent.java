package dev.shendriks.fitnesstrackerapi.domain.activity.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class ActivityUploadedEvent extends ApplicationEvent {
    @Getter
    private final Long userId;
    @Getter
    private final Long activityId;

    public ActivityUploadedEvent(Object source, Long userId, Long activityId) {
        super(source);
        this.userId = userId;
        this.activityId = activityId;
    }
}
