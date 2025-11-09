package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.user;

import dev.shendriks.fitnesstrackerapi.domain.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record UserResponseDTO(
    @Schema(description = "Unique identifier of the user", example = "01K9JBNQ35NXGP80XGH6WW4H0R", type = "string", format = "ulid")
    String id,
    @Schema(description = "Display name of the user", example = "John Doe", type = "string")
    String name,
    @Schema(description = "Email address of the user", example = "john.doe@example.com", type = "string", format = "email")
    String email,
    @Schema(implementation = AccountType.class, description = "Account type for the user", example = "basic")
    AccountType accountType,
    @Schema(description = "Creation timestamp (RFC3339)", example = "2023-01-01T10:00:00Z", type = "string", format = "date-time")
    Instant createdAt,
    @Schema(description = "Last update timestamp (RFC3339)", example = "2023-01-02T08:30:00Z", type = "string", format = "date-time")
    Instant updatedAt
) {
}
