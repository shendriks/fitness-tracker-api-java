package dev.shendriks.fitnesstrackerapi.application.event;

import dev.shendriks.fitnesstrackerapi.domain.value.MilestoneId;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class MilestoneBecameIncompleteEvent extends ApplicationEvent {
    @Getter
    private final UserId userId;
    @Getter
    private final MilestoneId milestoneId;

    public MilestoneBecameIncompleteEvent(Object source, UserId userId, MilestoneId milestoneId) {
        super(source);
        this.userId = userId;
        this.milestoneId = milestoneId;
    }
}
