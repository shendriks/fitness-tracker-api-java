package dev.shendriks.fitnesstrackerapi.domain.value;

import dev.shendriks.fitnesstrackerapi.domain.enums.AccountType;

public record UserSignupData(
    String name,
    String email,
    String password,
    AccountType accountType
) {
}
