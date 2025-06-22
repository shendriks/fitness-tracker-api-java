package dev.shendriks.fitnesstrackerapi.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.shendriks.fitnesstrackerapi.application.enums.Category;

public record ApplicationResponse(
        Long id,
        String name,
        String description,
        Category category,
        @JsonProperty("apikey")
        String apiKey
) {
}
