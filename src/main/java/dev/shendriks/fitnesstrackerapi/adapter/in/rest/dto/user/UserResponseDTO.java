package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.user;

import dev.shendriks.fitnesstrackerapi.domain.enums.AccountType;

import java.time.Instant;

public record UserResponseDTO(
    String id,
    String name,
    String email,
    AccountType accountType,
    Instant createdAt,
    Instant updatedAt
) {
}
