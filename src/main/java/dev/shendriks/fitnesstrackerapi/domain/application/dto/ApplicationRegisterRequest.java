package dev.shendriks.fitnesstrackerapi.domain.application.dto;

import dev.shendriks.fitnesstrackerapi.domain.application.enums.Category;
import dev.shendriks.fitnesstrackerapi.validation.enums.ValueOfEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ApplicationRegisterRequest(
    @NotBlank
    String name,
    @NotNull
    String description,
    @NotBlank
    @Schema(implementation = Category.class)
    @ValueOfEnum(enumClass = Category.class)
    String category
) {
}
