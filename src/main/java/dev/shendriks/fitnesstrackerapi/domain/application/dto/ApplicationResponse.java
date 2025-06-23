package dev.shendriks.fitnesstrackerapi.domain.application.dto;

import dev.shendriks.fitnesstrackerapi.domain.application.enums.Category;

public record ApplicationResponse(
    String id,
    String name,
    String description,
    Category category,
    String apiKey
) {
}
