package dev.shendriks.fitnesstrackerapi.domain.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.shendriks.fitnesstrackerapi.domain.application.enums.Category;

public record ApplicationRegisterResponse(
        String name,
        @JsonProperty("apikey")
        String apiKey,
        Category category
) {
}
