package dev.shendriks.fitnesstrackerapi.application;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApplicationRegisterResponse(
        String name,
        @JsonProperty("apikey")
        String apiKey,
        Category category
) {
}
