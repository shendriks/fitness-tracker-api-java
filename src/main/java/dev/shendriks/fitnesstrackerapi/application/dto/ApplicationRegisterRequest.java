package dev.shendriks.fitnesstrackerapi.application.dto;

import dev.shendriks.fitnesstrackerapi.application.enums.Category;
import dev.shendriks.fitnesstrackerapi.validation.enums.ValueOfEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ApplicationRegisterRequest(
    @NotNull
    @NotBlank
    String name,
    @NotNull
    String description,
    @NotNull
    @Pattern(regexp = "^(basic|premium)$")
    @ValueOfEnum(enumClass = Category.class)
    String category
) {
}
