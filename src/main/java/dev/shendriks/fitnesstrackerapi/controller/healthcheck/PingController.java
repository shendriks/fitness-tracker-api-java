package dev.shendriks.fitnesstrackerapi.controller.healthcheck;

import dev.shendriks.fitnesstrackerapi.openapi.OpenApiTagName;
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
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("{\"message\": \"Pong!\"}");
    }
}
