package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.user;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.validation.enums.ValueOfEnum;
import dev.shendriks.fitnesstrackerapi.domain.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserSignupRequestDTO(
    @NotBlank
    @Schema(description = "Full name of the user", example = "John Doe", type = "string")
    String name,
    @NotBlank
    @Email
    @Schema(description = "Email address of the user", example = "john.doe@example.com", type = "string", format = "email")
    String email,
    @NotBlank
    @Schema(description = "Password for the new account", example = "Str0ngP@ssw0rd!", type = "string", format = "password")
    String password,
    @NotBlank
    @Schema(implementation = AccountType.class, description = "Type of account to create", example = "basic")
    @ValueOfEnum(enumClass = AccountType.class)
    String accountType
) {
}
