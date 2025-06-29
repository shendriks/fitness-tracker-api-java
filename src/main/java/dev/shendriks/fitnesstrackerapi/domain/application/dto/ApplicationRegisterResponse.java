package dev.shendriks.fitnesstrackerapi.domain.application.dto;

import dev.shendriks.fitnesstrackerapi.domain.user.enums.AccountType;

public record ApplicationRegisterResponse(
    String id,
    String name,
    String apiKey,
    AccountType accountType
) {
}
