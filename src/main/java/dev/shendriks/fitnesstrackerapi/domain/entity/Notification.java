package dev.shendriks.fitnesstrackerapi.domain.entity;

import dev.shendriks.fitnesstrackerapi.domain.value.NotificationId;
import dev.shendriks.fitnesstrackerapi.domain.value.NotificationUlid;
import lombok.Builder;

import java.time.Instant;

@Builder
public record Notification(
    NotificationId id,
    NotificationUlid ulid,
    Instant createdAt,
    Instant updatedAt,
    String title,
    String description
) {
}
