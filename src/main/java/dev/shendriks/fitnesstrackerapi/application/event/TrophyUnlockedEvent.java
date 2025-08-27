package dev.shendriks.fitnesstrackerapi.application.event;

import dev.shendriks.fitnesstrackerapi.domain.value.TrophyId;
import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

public record TrophyUnlockedEvent(UserId userId, TrophyId trophyId) {
}
