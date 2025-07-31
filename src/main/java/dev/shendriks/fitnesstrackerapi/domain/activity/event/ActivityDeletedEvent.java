package dev.shendriks.fitnesstrackerapi.domain.activity.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class ActivityDeletedEvent extends ApplicationEvent {
    @Getter
    private final Long userId;
    @Getter
    private final String activityTitle;

    public ActivityDeletedEvent(Object source, Long userId, String activityTitle) {
        super(source);
        this.userId = userId;
        this.activityTitle = activityTitle;
    }
}
