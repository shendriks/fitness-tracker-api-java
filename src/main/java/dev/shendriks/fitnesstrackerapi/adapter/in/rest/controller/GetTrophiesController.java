package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.TrophyResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper.TrophyDTOMapper;
import dev.shendriks.fitnesstrackerapi.application.port.in.trophy.ListTrophiesUseCase;
import dev.shendriks.fitnesstrackerapi.domain.entity.Trophy;
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
@Tag(name = OpenApiTagName.TROPHIES)
@SecurityRequirement(name = BEARER_TOKEN)
@AllArgsConstructor
public class GetTrophiesController {
    private final ListTrophiesUseCase useCase;
    private final TrophyDTOMapper mapper;

    @Operation(summary = "Get user's trophies")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The user's trophies",
            content = {
                @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = TrophyResponseDTO.class)))
            }
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    })
    @GetMapping("/api/trophies")
    public ResponseEntity<Iterable<TrophyResponseDTO>> getTrophies(@AuthenticationPrincipal(errorOnInvalidType = true) User user) {
        List<Trophy> trophies = useCase.getAllTrophiesByUser(user.id());
        List<TrophyResponseDTO> response = mapper.toTrophyResponseDTOs(trophies);
        return ResponseEntity.ok(response);
    }
}
