package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AccessTokenResponseDTO(
    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", type = "string", format = "jwt")
    String token
) {
}
