package dev.shendriks.fitnesstrackerapi.domain.user.dto;

import dev.shendriks.fitnesstrackerapi.domain.user.enums.AccountType;

import java.time.Instant;

public record UserResponse(
    String id,
    String name,
    String email,
    AccountType accountType,
    Instant createdAt,
    Instant updatedAt
) {
}
