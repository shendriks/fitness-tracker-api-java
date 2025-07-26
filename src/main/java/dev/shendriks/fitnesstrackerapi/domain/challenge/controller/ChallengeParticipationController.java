package dev.shendriks.fitnesstrackerapi.domain.challenge.controller;

import dev.shendriks.fitnesstrackerapi.domain.challenge.dto.ChallengeParticipationResponse;
import dev.shendriks.fitnesstrackerapi.domain.challenge.service.ChallengeService;
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
@Tag(name = OpenApiTagName.CHALLENGES, description = "Challenges the user is participating in")
@RequestMapping("/api/challenge-participations")
@SecurityRequirement(name = BEARER_TOKEN)
public class ChallengeParticipationController {
    private final ChallengeService challengeService;

    public ChallengeParticipationController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @Operation(summary = "Get the user's challenge participations")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "All challenge participations",
            content = {
                @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ChallengeParticipationResponse.class)))
            }
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    @GetMapping
    public ResponseEntity<Iterable<ChallengeParticipationResponse>> getChallenges(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(challengeService.getAllChallengeParticipations(user));
    }
}
