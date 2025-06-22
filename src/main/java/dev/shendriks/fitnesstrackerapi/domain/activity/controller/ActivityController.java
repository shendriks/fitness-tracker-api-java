package dev.shendriks.fitnesstrackerapi.domain.activity.controller;

import dev.shendriks.fitnesstrackerapi.domain.activity.dto.ActivityRequest;
import dev.shendriks.fitnesstrackerapi.domain.activity.dto.ActivityResponse;
import dev.shendriks.fitnesstrackerapi.domain.activity.service.ActivityService;
import dev.shendriks.fitnesstrackerapi.domain.application.entity.Application;
import dev.shendriks.fitnesstrackerapi.error.ApiError;
import dev.shendriks.fitnesstrackerapi.ratelimiting.RateLimitExceededException;
import dev.shendriks.fitnesstrackerapi.ratelimiting.RateLimiterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/activities")
@Tag(name = "Activity", description = "An activityType is a record of a user's exercise, e.g. running or swimming")
@SecurityRequirement(name = "API Key")
public class ActivityController {
    private final ActivityService activityService;
    private final RateLimiterService rateLimiterService;

    public ActivityController(ActivityService activityService, RateLimiterService rateLimiterService) {
        this.activityService = activityService;
        this.rateLimiterService = rateLimiterService;
    }

    @Operation(summary = "Create a new activityType")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Activity created",
            content = @Content,
            headers = {@Header(
                name = "Location",
                description = "The URI of the created activityType",
                schema = @Schema(type = "string")
            )}
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid data",
            content = @Content(schema = @Schema(implementation = ApiError.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded", content = @Content)})
    @PostMapping
    public ResponseEntity<Void> postActivity(
        @AuthenticationPrincipal Application application,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "The activityType to create",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ActivityRequest.class),
                examples = @ExampleObject(value = """
                        {
                            "username": "user1",
                            "activityType": "running",
                            "duration": 60,
                            "calories": 450
                        }
                    """)))
        @RequestBody @Valid ActivityRequest request
    ) {
        if (!rateLimiterService.isRequestAllowed(application)) {
            throw new RateLimitExceededException();
        }

        ActivityResponse activityResponse = activityService.save(request, application);
        URI location = URI.create("/api/activities" + activityResponse.id());

        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Get all activities")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Activities",
            content = {
                @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ActivityResponse.class)))
            }
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded", content = @Content)})
    @GetMapping
    public ResponseEntity<Iterable<ActivityResponse>> getActivities(@AuthenticationPrincipal Application application) {
        if (!rateLimiterService.isRequestAllowed(application)) {
            throw new RateLimitExceededException();
        }

        Iterable<ActivityResponse> activities = activityService.getAllActivities();

        return ResponseEntity.ok(activities);
    }
}
