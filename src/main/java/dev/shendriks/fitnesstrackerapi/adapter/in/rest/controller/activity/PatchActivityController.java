package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller.activity;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.ApiErrorResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity.ActivityResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity.ActivityUpdateRequestDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper.ActivityDTOMapper;
import dev.shendriks.fitnesstrackerapi.application.port.in.activity.UpdateActivityUseCase;
import dev.shendriks.fitnesstrackerapi.domain.entity.Activity;
import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityUlid;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityUpdateData;
import dev.shendriks.fitnesstrackerapi.infrastructure.openapi.OpenApiTagName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static dev.shendriks.fitnesstrackerapi.infrastructure.security.SecurityRequirementName.BEARER_TOKEN;

@RestController
@Tag(name = OpenApiTagName.ACTIVITIES)
@SecurityRequirement(name = BEARER_TOKEN)
@AllArgsConstructor
public class PatchActivityController {
    private final ActivityDTOMapper activityMapper;
    private final UpdateActivityUseCase updateActivityUseCase;

    @Operation(summary = "Update activity")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The updated activity",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid data",
            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded", content = @Content)})
    @PatchMapping("/api/activities/{id}")
    public ResponseEntity<ActivityResponseDTO> patchActivity(
        @AuthenticationPrincipal(errorOnInvalidType = true) User user,
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
                schema = @Schema(implementation = ActivityUpdateRequestDTO.class),
                examples = @ExampleObject(value = """
                        {
                            "activityType": "running",
                            "title": "Morning Run",
                            "description": "Some description."
                        }
                    """)))
        @RequestBody @Valid ActivityUpdateRequestDTO request
    ) {
//        if (!rateLimiterService.isRequestAllowed(user)) {
//            log.info("Rate limit exceeded for user " + user.getId() + " (" + user.getEmail() + ")");
//            throw new RateLimitExceededException();
//        }
//
        ActivityUpdateData activityUpdateData = activityMapper.toActivityUpdateData(request);
        Activity activity = updateActivityUseCase.updateActivityForUser(
            user.id(),
            new ActivityUlid(id),
            activityUpdateData
        );
        ActivityResponseDTO response = activityMapper.toActivityResponse(activity);

        return ResponseEntity.ok(response);
    }
}
