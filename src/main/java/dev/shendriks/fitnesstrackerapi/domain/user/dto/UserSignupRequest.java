package dev.shendriks.fitnesstrackerapi.domain.user.dto;

import dev.shendriks.fitnesstrackerapi.domain.user.enums.AccountType;
import dev.shendriks.fitnesstrackerapi.validation.enums.ValueOfEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserSignupRequest(
    @NotBlank
    String name,
    @NotBlank
    @Email
    String email,
    @NotBlank
    String password,
    @NotBlank
    @Schema(implementation = AccountType.class)
    @ValueOfEnum(enumClass = AccountType.class)
    String accountType
) {
}
