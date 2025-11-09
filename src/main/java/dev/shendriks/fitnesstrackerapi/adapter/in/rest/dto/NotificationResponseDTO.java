package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public record NotificationResponseDTO(
    @Schema(description = "Unique identifier of the notification", example = "01K9JBZ5GJPDEJZCWK9ERY1MYK", type = "string", format = "ulid")
    String id,
    @Schema(description = "Title of the notification", example = "Welcome!", type = "string")
    String title,
    @Schema(description = "Body of the notification", example = "Thanks for signing up!", type = "string")
    String description,
    @Schema(description = "Creation timestamp (RFC3339)", example = "2023-01-01T09:30:00Z", type = "string", format = "date-time")
    Instant createdAt
) {
}
