package dev.shendriks.fitnesstrackerapi.domain.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.shendriks.fitnesstrackerapi.domain.application.enums.Category;

public record ApplicationResponse(
        Long id,
        String name,
        String description,
        Category category,
        @JsonProperty("apikey")
        String apiKey
) {
}
