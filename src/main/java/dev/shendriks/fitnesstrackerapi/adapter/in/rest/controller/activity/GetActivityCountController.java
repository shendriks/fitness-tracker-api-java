package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller.activity;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity.ActivityCountResponseDTO;
import dev.shendriks.fitnesstrackerapi.application.port.in.activity.CountActivitiesUseCase;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static dev.shendriks.fitnesstrackerapi.infrastructure.security.SecurityRequirementName.BEARER_TOKEN;

@RestController
@Tag(name = OpenApiTagName.ACTIVITIES)
@SecurityRequirement(name = BEARER_TOKEN)
@AllArgsConstructor
public class GetActivityCountController {
    private final CountActivitiesUseCase forGettingActivityCountByUser;

    @Operation(summary = "Get user's activity count")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Activity count",
            content = {
                @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ActivityCountResponseDTO.class)))
            }
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded", content = @Content)})
    @GetMapping("/api/activities/count")
    public ResponseEntity<ActivityCountResponseDTO> getActivityCount(
        @AuthenticationPrincipal(errorOnInvalidType = true) User user
    ) {
        long activityCount = forGettingActivityCountByUser.getActivityCountByUser(user.id());
        ActivityCountResponseDTO activityCountResponse = new ActivityCountResponseDTO(activityCount);
        return ResponseEntity.ok(activityCountResponse);
    }
}
