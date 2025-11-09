package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.user;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.validation.enums.ValueOfEnum;
import dev.shendriks.fitnesstrackerapi.domain.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequestDTO(
    @NotBlank
    @Schema(description = "Updated full name of the user", example = "Jane Doe", type = "string")
    String name,
    @NotBlank
    @Email
    @Schema(description = "Updated email address of the user", example = "jane.doe@example.com", type = "string", format = "email")
    String email,
    @NotBlank
    @Schema(description = "New password for the account", example = "N3wStr0ngP@ss!", type = "string", format = "password")
    String newPassword,
    @NotBlank
    @Schema(description = "Current password to authorize the change", example = "Curr3ntP@ss!", type = "string", format = "password")
    String currentPassword,
    @NotBlank
    @Schema(implementation = AccountType.class, description = "Updated account type", example = "premium")
    @ValueOfEnum(enumClass = AccountType.class)
    String accountType
) {
}
