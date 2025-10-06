package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto;

import java.util.Collections;
import java.util.List;

public record ApiErrorResponseDTO(String message, List<String> errors) {

    public ApiErrorResponseDTO(String message) {
        this("An error occurred", Collections.singletonList(message));
    }

    public ApiErrorResponseDTO(String message, String error) {
        this(message, Collections.singletonList(error));
    }
}