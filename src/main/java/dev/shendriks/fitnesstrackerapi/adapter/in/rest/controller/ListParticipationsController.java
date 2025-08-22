package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.ApiErrorResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.ChallengeParticipationResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper.ChallengeParticipationDTOMapper;
import dev.shendriks.fitnesstrackerapi.application.port.in.challengeparticipation.ListChallengeParticipationsUseCase;
import dev.shendriks.fitnesstrackerapi.domain.entity.ChallengeParticipation;
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
@Tag(name = OpenApiTagName.CHALLENGES, description = "Challenges the user is participating in")
@SecurityRequirement(name = BEARER_TOKEN)
@AllArgsConstructor
public class ListParticipationsController {
    private final ListChallengeParticipationsUseCase listChallengeParticipationsUseCase;
    private final ChallengeParticipationDTOMapper mapper;

    @Operation(summary = "Get the user's challenge participations")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "All challenge participations",
            content = {
                @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ChallengeParticipationResponseDTO.class)))
            }
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
    })
    @GetMapping("/api/challenge-participations")
    public ResponseEntity<Iterable<ChallengeParticipationResponseDTO>> listChallengeParticipations(
        @AuthenticationPrincipal(errorOnInvalidType = true)
        User user
    ) {
        List<ChallengeParticipation> challengeParticipations = listChallengeParticipationsUseCase.getAllChallengeParticipations(user.id());
        List<ChallengeParticipationResponseDTO> challengeParticipationResponseDTOs = mapper.toChallengeParticipationResponseDTOs(challengeParticipations);
        return ResponseEntity.ok(challengeParticipationResponseDTOs);
    }
}
