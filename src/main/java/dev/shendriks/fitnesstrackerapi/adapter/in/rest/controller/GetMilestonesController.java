package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.ApiErrorResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.MilestoneResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper.MilestoneDTOMapper;
import dev.shendriks.fitnesstrackerapi.application.port.in.milestone.ListMilestonesUseCase;
import dev.shendriks.fitnesstrackerapi.domain.entity.Milestone;
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

import java.util.List;

import static dev.shendriks.fitnesstrackerapi.infrastructure.security.SecurityRequirementName.BEARER_TOKEN;

@RestController
@Tag(name = OpenApiTagName.MILESTONES)
@SecurityRequirement(name = BEARER_TOKEN)
@AllArgsConstructor
public class GetMilestonesController {
    private final ListMilestonesUseCase useCase;
    private final MilestoneDTOMapper mapper;

    @Operation(summary = "Get all milestones")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "All milestones",
            content = {
                @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = MilestoneResponseDTO.class)))
            }
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
    })
    @GetMapping("/api/milestones")
    public ResponseEntity<List<MilestoneResponseDTO>> getMilestones(@AuthenticationPrincipal(errorOnInvalidType = true) User user) {
        List<Milestone> milestones = useCase.getAllMilestonesWithCompletedByUser(user.id());
        List<MilestoneResponseDTO> milestoneReponseDTOs = mapper.toMilestoneResponseDTOs(milestones);
        return ResponseEntity.ok(milestoneReponseDTOs);
    }
}
