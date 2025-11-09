package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PingResponseDTO(
    @Schema(description = "Ping response message", example = "Pong!", type = "string")
    String message
) {
}
