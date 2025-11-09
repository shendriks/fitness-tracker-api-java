package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collections;
import java.util.List;

public record ApiErrorResponseDTO(
    @Schema(description = "Human readable error message", example = "Validation failed", type = "string")
    String message,
    @Schema(description = "List of specific error details", example = "[\"duration must be >= 0\", \"email is invalid\"]", type = "array")
    List<String> errors
) {

    public ApiErrorResponseDTO(String message) {
        this("An error occurred", Collections.singletonList(message));
    }

    public ApiErrorResponseDTO(String message, String error) {
        this(message, Collections.singletonList(error));
    }
}