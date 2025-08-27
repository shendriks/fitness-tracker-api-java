package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class ApiErrorResponseDTO {
    private final String message;
    private final List<String> errors;

    public ApiErrorResponseDTO(String message, List<String> errors) {
        this.message = message;
        this.errors = errors;
    }

    public ApiErrorResponseDTO(String message) {
        this.message = message;
        errors = Collections.singletonList(message);
    }

    public ApiErrorResponseDTO(String message, String error) {
        this.message = message;
        errors = Collections.singletonList(error);
    }
}