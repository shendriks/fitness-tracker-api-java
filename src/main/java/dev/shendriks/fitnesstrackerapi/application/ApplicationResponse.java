package dev.shendriks.fitnesstrackerapi.application;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApplicationResponse(
        Long id,
        String name,
        String description,
        Category category,
        @JsonProperty("apikey")
        String apiKey
) {
}
