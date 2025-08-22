package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller.activity;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.ApiErrorResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity.ActivityCreateRequestDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity.ActivityResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper.ActivityDTOMapper;
import dev.shendriks.fitnesstrackerapi.application.port.in.activity.CreateActivityUseCase;
import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityCreationData;
import dev.shendriks.fitnesstrackerapi.infrastructure.openapi.OpenApiTagName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static dev.shendriks.fitnesstrackerapi.infrastructure.security.SecurityRequirementName.BEARER_TOKEN;

@Log
@RestController
@Tag(name = OpenApiTagName.ACTIVITIES)
@SecurityRequirement(name = BEARER_TOKEN)
@AllArgsConstructor
public class PostActivityController {
    private final CreateActivityUseCase forCreatingAnActivity;
    private final ActivityDTOMapper activityMapper;

    @Operation(summary = "Manually create a new activity")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The created activity",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid data",
            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded", content = @Content)})
    @PostMapping("/api/activities")
    public ResponseEntity<ActivityResponseDTO> postActivity(
        @AuthenticationPrincipal(errorOnInvalidType = true) User user,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "The activity to create",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ActivityCreateRequestDTO.class),
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
        @RequestBody
        @Valid
        ActivityCreateRequestDTO request
    ) {
//        if (!rateLimiterService.isRequestAllowed(user)) {
//            log.info("Rate limit exceeded for user " + user.getId() + " (" + user.getEmail() + ")");
//            throw new RateLimitExceededException();
//        }
//
        ActivityCreationData activityCreationData = activityMapper.toActivityCreationData(request);
        Activity activity = forCreatingAnActivity.saveActivityForUser(user.id(), activityCreationData);
        ActivityResponseDTO activityResponse = activityMapper.toActivityResponse(activity);

        return ResponseEntity.ok(activityResponse);
    }
}
