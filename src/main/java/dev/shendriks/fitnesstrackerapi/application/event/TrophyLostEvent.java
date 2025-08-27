package dev.shendriks.fitnesstrackerapi.application.event;

import dev.shendriks.fitnesstrackerapi.domain.value.UserId;

public record TrophyLostEvent(UserId userId) {
}
