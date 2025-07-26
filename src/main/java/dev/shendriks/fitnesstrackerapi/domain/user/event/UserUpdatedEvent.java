package dev.shendriks.fitnesstrackerapi.domain.user.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserUpdatedEvent extends ApplicationEvent {
    private final Long userId;

    public UserUpdatedEvent(Object source, Long userId) {
        super(source);
        this.userId = userId;
    }
}
