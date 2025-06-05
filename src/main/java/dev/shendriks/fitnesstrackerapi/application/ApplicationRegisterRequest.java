package dev.shendriks.fitnesstrackerapi.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ApplicationRegisterRequest(
        @NotNull
        @NotBlank
        String name,
        @NotNull
        String description,
        @NotNull
        Category category
) {
}
