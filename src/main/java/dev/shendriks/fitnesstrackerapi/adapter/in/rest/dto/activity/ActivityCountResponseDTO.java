package dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity;

import io.swagger.v3.oas.annotations.media.Schema;

public record ActivityCountResponseDTO(
    @Schema(example = "17", description = "The number of activities", type = "integer", format = "int64")
    Long count
) {
}
