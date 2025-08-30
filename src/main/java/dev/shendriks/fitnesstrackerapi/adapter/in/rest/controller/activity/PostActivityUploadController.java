package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller.activity;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.ApiErrorResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity.ActivityDetailsResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.activity.ActivityUploadRequestDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper.ActivityDTOMapper;
import dev.shendriks.fitnesstrackerapi.application.port.in.activity.UploadActivityUseCase;
import dev.shendriks.fitnesstrackerapi.domain.entity.ActivityDetails;
import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.value.ActivityUploadData;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static dev.shendriks.fitnesstrackerapi.infrastructure.security.SecurityRequirementName.BEARER_TOKEN;

@RestController
@Tag(name = OpenApiTagName.ACTIVITIES)
@SecurityRequirement(name = BEARER_TOKEN)
@AllArgsConstructor
public class PostActivityUploadController {
    private final ActivityDTOMapper activityMapper;
    private final UploadActivityUseCase uploadActivityUseCase;

    @Operation(summary = "Upload a new activity")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Upload processed successfully",
            content = {
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ActivityDetailsResponseDTO.class))
            }
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid data",
            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded", content = @Content)})
    @PostMapping(value = "/api/activities/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ActivityDetailsResponseDTO> uploadActivity(
        @AuthenticationPrincipal(errorOnInvalidType = true) User user,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "multipart/form-data",
                schema = @Schema(implementation = ActivityUploadRequestDTO.class)
            ))
        @ModelAttribute
        @Valid
        ActivityUploadRequestDTO request
    ) {
        ActivityUploadData activityUploadData = activityMapper.toActivityUploadData(request);
        ActivityDetails activity = uploadActivityUseCase.uploadActivityForUser(user.id(), activityUploadData);
        ActivityDetailsResponseDTO activityDetailsResponse = activityMapper.toActivityDetailsResponse(activity);
        return ResponseEntity.ok(activityDetailsResponse);
    }
}
