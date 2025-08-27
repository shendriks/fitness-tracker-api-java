package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto;

import java.time.Instant;

public record NotificationResponseDTO(
    String id,
    String title,
    String description,
    Instant createdAt
) {
}
