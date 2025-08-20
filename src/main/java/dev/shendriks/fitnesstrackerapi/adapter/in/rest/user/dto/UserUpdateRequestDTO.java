package dev.shendriks.fitnesstrackerapi.adapter.in.rest.user.dto;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.validation.enums.ValueOfEnum;
import dev.shendriks.fitnesstrackerapi.domain.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequestDTO(
    @NotBlank
    String name,
    @NotBlank
    @Email
    String email,
    @NotBlank
    String newPassword,
    @NotBlank
    String currentPassword,
    @NotBlank
    @Schema(implementation = AccountType.class)
    @ValueOfEnum(enumClass = AccountType.class)
    String accountType
) {
}
