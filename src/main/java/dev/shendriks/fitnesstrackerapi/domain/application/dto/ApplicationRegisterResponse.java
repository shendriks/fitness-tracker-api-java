package dev.shendriks.fitnesstrackerapi.domain.application.dto;

import dev.shendriks.fitnesstrackerapi.domain.application.enums.Category;

public record ApplicationRegisterResponse(
    String id,
    String name,
    String apiKey,
    Category category
) {
}
