package dev.shendriks.fitnesstrackerapi.domain.value;

import dev.shendriks.fitnesstrackerapi.domain.enums.AccountType;
import lombok.Builder;

@Builder
public record UserSignupData(
    String name,
    String email,
    String password,
    AccountType accountType
) {
}
