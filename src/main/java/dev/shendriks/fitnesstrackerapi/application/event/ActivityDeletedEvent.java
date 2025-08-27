package dev.shendriks.fitnesstrackerapi.application.event;

import dev.shendriks.fitnesstrackerapi.domain.value.ActivityUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

public record ActivityDeletedEvent(UserId userId, ActivityUlid activityTitle) {
}
