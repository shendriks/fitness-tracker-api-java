package dev.shendriks.fitnesstrackerapi.application.event;

import dev.shendriks.fitnesstrackerapi.domain.value.ActivityId;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

public record ActivityUpdatedEvent(UserId userId, ActivityId activityId) {
}
