package dev.shendriks.fitnesstrackerapi.domain.activity.controller;

import dev.shendriks.fitnesstrackerapi.domain.activity.dto.*;
import dev.shendriks.fitnesstrackerapi.domain.activity.service.ActivityService;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import dev.shendriks.fitnesstrackerapi.error.ApiError;
import dev.shendriks.fitnesstrackerapi.openapi.OpenApiTagName;
import dev.shendriks.fitnesstrackerapi.ratelimiting.RateLimitExceededException;
import dev.shendriks.fitnesstrackerapi.ratelimiting.RateLimiterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    public ActivityController(
        ActivityService activityService,
        RateLimiterService rateLimiterService
    ) {
        this.activityService = activityService;
        this.rateLimiterService = rateLimiterService;
    }

    @Operation(summary = "Manually create a new activity")
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
                schema = @Schema(implementation = ActivityCreateRequest.class),
                examples = @ExampleObject(value = """
                        {
                            "activityType": "running",
                            "duration": 60,
                            "calories": 450,
                            "title": "Morning Run",
                            "description": "Some description.",
                            "distance": 2500,
                            "startDate": "2025-07-18T21:08:00+02:00"
                        }
                    """)))
        @RequestBody @Valid ActivityCreateRequest request
    ) {
        if (!rateLimiterService.isRequestAllowed(user)) {
            log.info("Rate limit exceeded for user " + user.getId() + " (" + user.getEmail() + ")");
            throw new RateLimitExceededException();
        }

        ActivityResponse activityResponse = activityService.save(user, request);

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

    @Operation(summary = "Get a user's activity")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Activity",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ActivityResponse.class))
            }
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded", content = @Content)})
    @GetMapping("/{id}")
//    @JsonView(View.WithGpsPositions.class)
    public ResponseEntity<ActivityResponse> getActivity(
        @AuthenticationPrincipal User user,
        @PathVariable
        @Parameter(
            name = "id",
            description = "The activity id",
            required = true,
            examples = @ExampleObject(value = "01JZQZZYETYBWQV7KF0BHGMBDJ")
        )
        String id
    ) {
        if (!rateLimiterService.isRequestAllowed(user)) {
            log.info("Rate limit exceeded for user " + user.getId() + " (" + user.getEmail() + ")");
            throw new RateLimitExceededException();
        }

        ActivityResponse activity = activityService.findActivityByUserAndId(user, id);
        
        return ResponseEntity.ok(activity);
    }

    @Operation(summary = "Get user's activity count")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Activity count",
            content = {
                @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ActivityCountResponse.class)))
            }
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded", content = @Content)})
    @GetMapping("/count")
    public ResponseEntity<ActivityCountResponse> getActivityCount(@AuthenticationPrincipal User user) {
        if (!rateLimiterService.isRequestAllowed(user)) {
            log.info("Rate limit exceeded for user " + user.getId() + " (" + user.getEmail() + ")");
            throw new RateLimitExceededException();
        }

        ActivityCountResponse activityCount = activityService.getActivityCountByUser(user);

        return ResponseEntity.ok(activityCount);
    }

    @Operation(summary = "Delete activity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Activity deleted", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded", content = @Content)})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(
        @AuthenticationPrincipal User user,
        @PathVariable
        @Parameter(
            name = "id",
            description = "The activity id",
            required = true,
            examples = @ExampleObject(value = "01JZQZZYETYBWQV7KF0BHGMBDJ")
        )
        String id
    ) {
        activityService.deleteActivity(user, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update activity")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Activity updated",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid data",
            content = @Content(schema = @Schema(implementation = ApiError.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not fodun", content = @Content),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded", content = @Content)})
    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchActivity(
        @AuthenticationPrincipal User user,
        @PathVariable
        @Parameter(
            name = "id",
            description = "The activity id",
            required = true,
            examples = @ExampleObject(value = "01JZQZZYETYBWQV7KF0BHGMBDJ")
        )
        String id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "The activity update",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ActivityUpdateRequest.class),
                examples = @ExampleObject(value = """
                        {
                            "activityType": "running",
                            "title": "Morning Run",
                            "description": "Some description."
                        }
                    """)))
        @RequestBody @Valid ActivityUpdateRequest request
    ) {
        if (!rateLimiterService.isRequestAllowed(user)) {
            log.info("Rate limit exceeded for user " + user.getId() + " (" + user.getEmail() + ")");
            throw new RateLimitExceededException();
        }

        activityService.update(user, id, request);
        
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Start recording a new activity")
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
    @PostMapping("/start")
    public ResponseEntity<Void> startActivity(
        @AuthenticationPrincipal User user,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "The activity to start recording",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = StartActivityRequest.class),
                examples = @ExampleObject(value = """
                        {
                            "activityType": "running"
                        }
                    """)))
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
