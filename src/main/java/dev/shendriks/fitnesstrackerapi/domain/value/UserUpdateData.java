package dev.shendriks.fitnesstrackerapi.domain.value;

import dev.shendriks.fitnesstrackerapi.domain.enums.AccountType;

public record UserUpdateData(
    String name,
    String email,
    String newPassword,
    String currentPassword,
    AccountType accountType
) {
}
