package dev.shendriks.fitnesstrackerapi.domain.developer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record DeveloperSignupRequest(
    @NotNull
    @NotEmpty
    @Email
    String email,
    @NotNull
    @NotEmpty
    String password
) {
}
