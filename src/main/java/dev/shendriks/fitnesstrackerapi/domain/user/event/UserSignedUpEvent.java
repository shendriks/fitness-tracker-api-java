package dev.shendriks.fitnesstrackerapi.domain.user.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserSignedUpEvent extends ApplicationEvent {
    private final Long userId;

    public UserSignedUpEvent(Object source, Long userId) {
        super(source);
        this.userId = userId;
    }
}
