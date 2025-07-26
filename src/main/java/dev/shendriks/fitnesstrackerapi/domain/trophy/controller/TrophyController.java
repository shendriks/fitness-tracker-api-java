package dev.shendriks.fitnesstrackerapi.domain.trophy.controller;

import dev.shendriks.fitnesstrackerapi.domain.trophy.dto.TrophyResponse;
import dev.shendriks.fitnesstrackerapi.domain.trophy.service.TrophyService;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import dev.shendriks.fitnesstrackerapi.openapi.OpenApiTagName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static dev.shendriks.fitnesstrackerapi.security.SecurityRequirementName.BEARER_TOKEN;

@RestController
@Tag(name = OpenApiTagName.TROPHIES, description = "A user's trophies")
@RequestMapping("/api/trophies")
@SecurityRequirement(name = BEARER_TOKEN)
public class TrophyController {
    private final TrophyService trophyService;

    public TrophyController(TrophyService trophyService) {
        this.trophyService = trophyService;
    }

    @Operation(summary = "Get user's trophies")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The user's trophies",
            content = {
                @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TrophyResponse.class)))
            }
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    })
    @GetMapping
    public ResponseEntity<Iterable<TrophyResponse>> getTrophies(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(trophyService.getAllTrophiesByUser(user));
    }
}
