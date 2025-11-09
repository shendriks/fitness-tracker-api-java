package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller.activity;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.ApiErrorResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity.ActivityCreateRequestDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity.ActivityDetailsResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper.ActivityDTOMapper;
import dev.shendriks.fitnesstrackerapi.application.port.in.activity.CreateManualActivityUseCase;
import dev.shendriks.fitnesstrackerapi.domain.entity.ActivityDetails;
import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityCreationData;
import dev.shendriks.fitnesstrackerapi.infrastructure.openapi.OpenApiTagName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static dev.shendriks.fitnesstrackerapi.infrastructure.security.SecurityRequirementName.BEARER_TOKEN;

@RestController
@Tag(name = OpenApiTagName.ACTIVITIES)
@SecurityRequirement(name = BEARER_TOKEN)
@AllArgsConstructor
public class PostActivityController {
    private final CreateManualActivityUseCase forCreatingAnActivity;
    private final ActivityDTOMapper activityMapper;

    @Operation(summary = "Manually create a new activity")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The created activity",
            content = @Content(schema = @Schema(implementation = ActivityDetailsResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid data",
            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded", content = @Content)})
    @PostMapping("/api/activities")
    public ResponseEntity<ActivityDetailsResponseDTO> postActivity(
        @AuthenticationPrincipal(errorOnInvalidType = true) User user,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "The activity to create",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ActivityCreateRequestDTO.class)
            ))
        @RequestBody
        @Valid
        ActivityCreateRequestDTO request
    ) {
        ActivityCreationData activityCreationData = activityMapper.toActivityCreationData(request);
        ActivityDetails activity = forCreatingAnActivity.saveManualActivityForUser(user.id(), activityCreationData);
        ActivityDetailsResponseDTO activityResponse = activityMapper.toActivityDetailsResponse(activity);

        return ResponseEntity.ok(activityResponse);
    }
}
