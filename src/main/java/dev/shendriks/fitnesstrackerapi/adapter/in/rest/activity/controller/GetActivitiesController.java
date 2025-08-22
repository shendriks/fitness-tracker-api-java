package dev.shendriks.fitnesstrackerapi.adapter.in.rest.activity.controller;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.activity.dto.ActivityResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.activity.mapper.ActivityDTOMapper;
import dev.shendriks.fitnesstrackerapi.application.port.in.activity.ListActivitiesUseCase;
import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.infrastructure.openapi.OpenApiTagName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static dev.shendriks.fitnesstrackerapi.infrastructure.security.SecurityRequirementName.BEARER_TOKEN;

@Log
@RestController
@Tag(name = OpenApiTagName.ACTIVITIES)
@SecurityRequirement(name = BEARER_TOKEN)
@AllArgsConstructor
public class GetActivitiesController {
    private final ListActivitiesUseCase getAllActivitiesUseCase;
    private final ActivityDTOMapper activityMapper;

    @Operation(summary = "Get user's activities")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The user's activities",
            content = {
                @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ActivityResponseDTO.class)))
            }
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded", content = @Content)})
    @GetMapping("/api/activities")
    public ResponseEntity<Iterable<ActivityResponseDTO>> getActivities(
        @AuthenticationPrincipal(errorOnInvalidType = true)
        User user
    ) {
//        if (!rateLimiterService.isRequestAllowed(user)) {
//            log.info("Rate limit exceeded for user " + user.getId() + " (" + user.getEmail() + ")");
//            throw new RateLimitExceededException();
//        }
//
        List<Activity> activities = getAllActivitiesUseCase.getAllActivitiesByUser(user.id());
        List<ActivityResponseDTO> activityResponses = activityMapper.toActivityResponses(activities);
        return ResponseEntity.ok(activityResponses);
    }
}
