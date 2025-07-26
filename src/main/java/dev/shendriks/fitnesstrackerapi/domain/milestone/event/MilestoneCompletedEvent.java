package dev.shendriks.fitnesstrackerapi.domain.milestone.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class MilestoneCompletedEvent extends ApplicationEvent {
    @Getter
    private final Long userId;
    @Getter
    private final Long milestoneId;

    public MilestoneCompletedEvent(Object source, Long userId, Long milestoneId) {
        super(source);
        this.userId = userId;
        this.milestoneId = milestoneId;
    }
}
