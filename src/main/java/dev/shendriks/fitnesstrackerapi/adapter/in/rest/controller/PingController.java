package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.PingResponseDTO;
import dev.shendriks.fitnesstrackerapi.infrastructure.openapi.OpenApiTagName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ping")
@Tag(name = OpenApiTagName.PING, description = "Health check")
public class PingController {
    @Operation(summary = "Ping the API")
    @ApiResponse(responseCode = "200", description = "pong")
    @GetMapping
    public ResponseEntity<PingResponseDTO> ping() {
        return ResponseEntity.ok(new PingResponseDTO("Pong!"));
    }
}
