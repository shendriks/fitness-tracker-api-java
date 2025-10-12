package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller.activity;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity.ActivityDetailsResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity.ActivityStatsResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper.ActivityDTOMapper;
import dev.shendriks.fitnesstrackerapi.application.port.in.activity.ShowActivityStatsUseCase;
import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityAggregationMap;
import dev.shendriks.fitnesstrackerapi.infrastructure.openapi.OpenApiTagName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Optional;

import static dev.shendriks.fitnesstrackerapi.infrastructure.security.SecurityRequirementName.BEARER_TOKEN;

@RestController
@Tag(name = OpenApiTagName.ACTIVITIES)
@SecurityRequirement(name = BEARER_TOKEN)
@AllArgsConstructor
public class GetActivityStatsController {
    private final ShowActivityStatsUseCase forGettingActivityStats;
    private final ActivityDTOMapper activityMapper;

    @Operation(summary = "Get a user's activity stats")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Activity Stats",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ActivityStatsResponseDTO.class))
            }
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded", content = @Content)})
    @GetMapping("/api/activities/stats")
    public ResponseEntity<ActivityStatsResponseDTO> getActivityStats(
        @AuthenticationPrincipal(errorOnInvalidType = true)
        User user,
        @Parameter(
            name = "start",
            description = "An optional start date (inclusive)",
            example = "2025-01-01T00:00:00+00:00"
        )
        Optional<Instant> start,
        @Parameter(
            name = "end",
            description = "An optional end date (exclusive)",
            example = "2026-01-01T00:00:00+00:00"
        )
        Optional<Instant> end
    ) {
        ActivityAggregationMap map = forGettingActivityStats.getActivityStatsByUser(user.id(), start.orElse(null), end.orElse(null));
        ActivityStatsResponseDTO activityResponse = activityMapper.toActivityStatsResponse(map);
        return ResponseEntity.ok(activityResponse);
    }
}
