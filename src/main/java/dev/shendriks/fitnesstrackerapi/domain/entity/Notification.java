package dev.shendriks.fitnesstrackerapi.domain.entity;

import dev.shendriks.fitnesstrackerapi.domain.value.NotificationId;
import dev.shendriks.fitnesstrackerapi.domain.value.NotificationUlid;

import java.time.Instant;

public record Notification(
    NotificationId id,
    NotificationUlid ulid,
    Instant createdAt,
    Instant updatedAt,
    String title,
    String description
) {
}
