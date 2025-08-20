package dev.shendriks.fitnesstrackerapi.application.event;

import dev.shendriks.fitnesstrackerapi.domain.value.ActivityUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class ActivityDeletedEvent extends ApplicationEvent {
    @Getter
    private final UserId userId;
    @Getter
    private final ActivityUlid activityTitle;

    public ActivityDeletedEvent(Object source, UserId userId, ActivityUlid activityUlid) {
        super(source);
        this.userId = userId;
        this.activityTitle = activityUlid;
    }
}
