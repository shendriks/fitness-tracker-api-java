package dev.shendriks.fitnesstrackerapi.application.event;

import dev.shendriks.fitnesstrackerapi.domain.value.MilestoneId;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

public record MilestoneBecameIncompleteEvent(UserId userId, MilestoneId milestoneId) {
}
