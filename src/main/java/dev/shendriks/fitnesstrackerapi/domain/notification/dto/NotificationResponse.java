package dev.shendriks.fitnesstrackerapi.domain.notification.dto;

import java.time.Instant;

public record NotificationResponse(
    String id,
    String title,
    String description,
    Instant createdAt
) {
}
