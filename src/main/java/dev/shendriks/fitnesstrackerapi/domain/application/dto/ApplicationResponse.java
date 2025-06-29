package dev.shendriks.fitnesstrackerapi.domain.application.dto;

import dev.shendriks.fitnesstrackerapi.domain.user.enums.AccountType;

public record ApplicationResponse(
    String id,
    String name,
    String description,
    AccountType accountType,
    String apiKey
) {
}
