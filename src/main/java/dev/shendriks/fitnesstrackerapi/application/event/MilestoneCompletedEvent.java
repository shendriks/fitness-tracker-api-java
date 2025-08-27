package dev.shendriks.fitnesstrackerapi.application.event;

import dev.shendriks.fitnesstrackerapi.domain.value.MilestoneId;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

public record MilestoneCompletedEvent(UserId userId, MilestoneId milestoneId) {
}
