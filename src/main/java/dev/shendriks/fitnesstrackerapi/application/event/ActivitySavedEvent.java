package dev.shendriks.fitnesstrackerapi.application.event;

import dev.shendriks.fitnesstrackerapi.domain.value.ActivityId;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class ActivitySavedEvent extends ApplicationEvent {
    @Getter
    private final UserId userId;
    @Getter
    private final ActivityId activityId;

    public ActivitySavedEvent(Object source, UserId userId, ActivityId activityId) {
        super(source);
        this.userId = userId;
        this.activityId = activityId;
    }
}
