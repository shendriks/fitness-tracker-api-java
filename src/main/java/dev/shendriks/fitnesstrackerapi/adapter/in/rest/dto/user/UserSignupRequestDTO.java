package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.user;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.validation.enums.ValueOfEnum;
import dev.shendriks.fitnesstrackerapi.domain.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserSignupRequestDTO(
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
