package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller.challenge;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.ApiErrorResponseDTO;
import dev.shendriks.fitnesstrackerapi.application.port.in.challenge.LeaveChallengeUseCase;
import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.value.ChallengeUlid;
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
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static dev.shendriks.fitnesstrackerapi.infrastructure.security.SecurityRequirementName.BEARER_TOKEN;

@AllArgsConstructor
@RestController
@Tag(name = OpenApiTagName.CHALLENGES)
@SecurityRequirement(name = BEARER_TOKEN)
public class LeaveChallengeController {
    private final LeaveChallengeUseCase challengeUseCases;

    @Operation(summary = "Leave challenge")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204"),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))),
    })
    @PostMapping("/api/challenges/{id}/leave")
    public ResponseEntity<Void> leaveChallenge(
        @AuthenticationPrincipal(errorOnInvalidType = true) User user,
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
        challengeUseCases.leaveChallenge(user.id(), new ChallengeUlid(id));
        return ResponseEntity.noContent().build();
    }
}
