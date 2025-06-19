package dev.shendriks.fitnesstrackerapi.application;

import dev.shendriks.fitnesstrackerapi.validation.enums.ValueOfEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ApplicationRegisterRequest(
    @NotNull
    @NotBlank
    String name,
    @NotNull
    String description,
    @NotNull
    @ValueOfEnum(enumClass = Category.class)
    String category
) {
}
