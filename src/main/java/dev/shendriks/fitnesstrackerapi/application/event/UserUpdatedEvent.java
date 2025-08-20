package dev.shendriks.fitnesstrackerapi.application.event;

import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserUpdatedEvent extends ApplicationEvent {
    private final UserId userId;

    public UserUpdatedEvent(Object source, UserId userId) {
        super(source);
        this.userId = userId;
    }
}
