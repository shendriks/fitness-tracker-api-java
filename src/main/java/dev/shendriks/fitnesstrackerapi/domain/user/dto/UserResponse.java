package dev.shendriks.fitnesstrackerapi.domain.user.dto;

import dev.shendriks.fitnesstrackerapi.domain.user.enums.AccountType;

public record UserResponse(
    String id,
    String email,
    AccountType accountType
) {
}
