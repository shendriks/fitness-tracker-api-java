package dev.shendriks.fitnesstrackerapi.adapter.in.rest.challenge.controller;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.challenge.dto.ChallengeResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.challenge.mapper.ChallengeDTOMapper;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.error.dto.ApiErrorResponseDTO;
import dev.shendriks.fitnesstrackerapi.application.port.in.challenge.ListChallengesUseCase;
import dev.shendriks.fitnesstrackerapi.domain.entity.Challenge;
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

@AllArgsConstructor
@RestController
@Tag(name = OpenApiTagName.CHALLENGES, description = "Users can master challenges and earn trophies")
@SecurityRequirement(name = BEARER_TOKEN)
public class GetChallengesController {
    private final ListChallengesUseCase getAllChallengesUseCase;
    private final ChallengeDTOMapper challengeMapper;

    @Operation(summary = "Get all challenges")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "All challenges",
            content = {
                @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ChallengeResponseDTO.class)))
            }
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
    })
    @GetMapping("/api/challenges")
    public ResponseEntity<List<ChallengeResponseDTO>> getChallenges(@AuthenticationPrincipal(errorOnInvalidType = true) User user) {
        List<Challenge> challenges = getAllChallengesUseCase.getAllChallenges(user.id());
        return ResponseEntity.ok(challengeMapper.toChallengeResponses(challenges));
    }
}
