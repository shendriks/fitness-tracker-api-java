package dev.shendriks.fitnesstrackerapi.domain.milestone.controller;

import dev.shendriks.fitnesstrackerapi.domain.milestone.dto.MilestoneResponse;
import dev.shendriks.fitnesstrackerapi.domain.milestone.service.MilestoneService;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import dev.shendriks.fitnesstrackerapi.error.ApiError;
import dev.shendriks.fitnesstrackerapi.openapi.OpenApiTagName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static dev.shendriks.fitnesstrackerapi.security.SecurityRequirementName.BEARER_TOKEN;

@RestController
@Tag(name = OpenApiTagName.MILESTONES, description = "Users can reach milestones by uploading activities")
@RequestMapping("/api/milestones")
@SecurityRequirement(name = BEARER_TOKEN)
public class MilestoneController {
    private final MilestoneService milestoneService;

    public MilestoneController(MilestoneService milestoneService) {
        this.milestoneService = milestoneService;
    }

    @Operation(summary = "Get all milestones")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "All milestones",
            content = {
                @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = MilestoneResponse.class)))
            }
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    @GetMapping
    public ResponseEntity<Iterable<MilestoneResponse>> getMilestones(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(milestoneService.findAllByUser(user));
    }
}
