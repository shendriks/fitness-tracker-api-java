package dev.shendriks.fitnesstrackerapi.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.shendriks.fitnesstrackerapi.application.enums.Category;

public record ApplicationRegisterResponse(
        String name,
        @JsonProperty("apikey")
        String apiKey,
        Category category
) {
}
