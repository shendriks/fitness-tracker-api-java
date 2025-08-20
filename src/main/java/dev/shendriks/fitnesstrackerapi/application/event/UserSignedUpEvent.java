package dev.shendriks.fitnesstrackerapi.application.event;

import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserSignedUpEvent extends ApplicationEvent {
    private final UserId userId;

    public UserSignedUpEvent(Object source, UserId userId) {
        super(source);
        this.userId = userId;
    }
}
