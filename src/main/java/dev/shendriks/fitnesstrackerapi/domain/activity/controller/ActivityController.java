package dev.shendriks.fitnesstrackerapi.domain.activity.controller;

import dev.shendriks.fitnesstrackerapi.domain.activity.dto.ActivityRequest;
import dev.shendriks.fitnesstrackerapi.domain.activity.dto.ActivityResponse;
import dev.shendriks.fitnesstrackerapi.domain.activity.dto.StartActivityRequest;
import dev.shendriks.fitnesstrackerapi.domain.activity.event.ActivityUploadedEvent;
import dev.shendriks.fitnesstrackerapi.domain.activity.service.ActivityService;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import dev.shendriks.fitnesstrackerapi.error.ApiError;
import dev.shendriks.fitnesstrackerapi.openapi.OpenApiTagName;
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
import lombok.extern.java.Log;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static dev.shendriks.fitnesstrackerapi.security.SecurityRequirementName.BEARER_TOKEN;

@Log
@RestController
@RequestMapping("/api/activities")
@Tag(name = OpenApiTagName.ACTIVITIES, description = "An activity is a record of a user's exercise, e.g. running, walking or swimming")
@SecurityRequirement(name = BEARER_TOKEN)
public class ActivityController {
    private final ActivityService activityService;
    private final RateLimiterService rateLimiterService;
    private final ApplicationEventPublisher eventPublisher;

    public ActivityController(
        ActivityService activityService, 
        RateLimiterService rateLimiterService,
        ApplicationEventPublisher eventPublisher
        ) {
        this.activityService = activityService;
        this.rateLimiterService = rateLimiterService;
        this.eventPublisher = eventPublisher;
    }

    @Operation(summary = "Create a new activity")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Activity created",
            content = @Content,
            headers = {@Header(
                name = "Location",
                description = "The URI of the created activity",
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
        @AuthenticationPrincipal User user,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "The activity to create",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ActivityRequest.class),
                examples = @ExampleObject(value = """
                        {
                            "activityType": "running",
                            "duration": 60,
                            "calories": 450
                        }
                    """)))
        @RequestBody @Valid ActivityRequest request
    ) {
        if (!rateLimiterService.isRequestAllowed(user)) {
            log.info("Rate limit exceeded for user " + user.getId() + " (" + user.getEmail() + ")");
            throw new RateLimitExceededException();
        }

        ActivityResponse activityResponse = activityService.save(request, user);
        
        eventPublisher.publishEvent(new ActivityUploadedEvent(this, user.getId()));
        
        URI location = URI.create("/api/activities/" + activityResponse.id());

        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Get user's activities")
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
    public ResponseEntity<Iterable<ActivityResponse>> getActivities(@AuthenticationPrincipal User user) {
        if (!rateLimiterService.isRequestAllowed(user)) {
            log.info("Rate limit exceeded for user " + user.getId() + " (" + user.getEmail() + ")");
            throw new RateLimitExceededException();
        }

        Iterable<ActivityResponse> activities = activityService.getAllActivitiesByUser(user);

        return ResponseEntity.ok(activities);
    }
    
    @PostMapping("/start")
    public ResponseEntity<Void> startActivity(
        @AuthenticationPrincipal User user, 
        @RequestBody @Valid StartActivityRequest request
    ) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/finish")
    public ResponseEntity<Void> finishActivity(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/gps-position")
    public ResponseEntity<Void> createGPSPositionForActivity(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok().build();
    }
}
