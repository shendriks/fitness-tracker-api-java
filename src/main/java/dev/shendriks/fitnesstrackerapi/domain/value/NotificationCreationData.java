package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.Builder;

@Builder
public record NotificationCreationData(
    UserId userId,
    String title,
    String description
) {
}
