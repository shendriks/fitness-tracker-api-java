package dev.shendriks.fitnesstrackerapi.domain.challenge.controller;

import dev.shendriks.fitnesstrackerapi.domain.challenge.dto.ChallengeResponse;
import dev.shendriks.fitnesstrackerapi.domain.challenge.entity.Challenge;
import dev.shendriks.fitnesstrackerapi.domain.challenge.service.ChallengeService;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import dev.shendriks.fitnesstrackerapi.error.ApiError;
import dev.shendriks.fitnesstrackerapi.openapi.OpenApiTagName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static dev.shendriks.fitnesstrackerapi.security.SecurityRequirementName.BEARER_TOKEN;

@RestController
@Tag(name = OpenApiTagName.CHALLENGES, description = "Users can master challenges and earn trophies")
@RequestMapping("/api/challenges")
@SecurityRequirement(name = BEARER_TOKEN)
public class ChallengeController {
    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @Operation(summary = "Get all challenges")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "All challenges",
            content = {
                @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ChallengeResponse.class)))
            }
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    @GetMapping
    public ResponseEntity<Iterable<ChallengeResponse>> getChallenges(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(challengeService.getAllChallenges(user));
    }

    @Operation(summary = "Join challenge")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204"
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    @PostMapping("/{id}/join")
    public ResponseEntity<Challenge> joinChallenge(
        @AuthenticationPrincipal User user,
        @PathVariable
        @Parameter(
            name = "id",
            description = "The challenge id",
            required = true,
            schema = @Schema(implementation = String.class),
            examples = @ExampleObject(value = "01K0PNG2QTMTD3E221WKEVVRMN")
        )
        String id
    ) {
        this.challengeService.joinChallenge(id, user);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Leave challenge")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204"
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ApiError.class))),
    })
    @PostMapping("/{id}/leave")
    public ResponseEntity<Void> leaveChallenge(
        @AuthenticationPrincipal User user,
        @PathVariable
        @Parameter(
            name = "id",
            description = "The challenge id",
            required = true,
            examples = @ExampleObject(value = "01K0PNG2QTMTD3E221WKEVVRMN")
        )
        String id
    ) {
        this.challengeService.leaveChallenge(id, user);
        return ResponseEntity.noContent().build();
    }
}
