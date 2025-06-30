package dev.shendriks.fitnesstrackerapi.domain.challenge.controller;

import dev.shendriks.fitnesstrackerapi.domain.challenge.dto.ChallengeResponse;
import dev.shendriks.fitnesstrackerapi.domain.challenge.service.ChallengeService;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
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
@Tag(name = OpenApiTagName.CHALLENGES, description = "Users can master challenges and earn achievements")
@RequestMapping("/api/challenges")
@SecurityRequirement(name = BEARER_TOKEN)
public class ChallengeController {
    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @Operation(summary = "Get all available challenges")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "All available challenges",
            content = {
                @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ChallengeResponse.class)))
            }
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    })
    @GetMapping
    public ResponseEntity<Iterable<ChallengeResponse>> getChallenges(@AuthenticationPrincipal User ignoredUser) {
        return ResponseEntity.ok(challengeService.getAllChallenges());
    }
}
