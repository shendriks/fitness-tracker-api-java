package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller.user;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.user.UserResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper.UserDTOMapper;
import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.infrastructure.openapi.OpenApiTagName;
import io.swagger.v3.oas.annotations.Operation;
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

import static dev.shendriks.fitnesstrackerapi.infrastructure.security.SecurityRequirementName.BEARER_TOKEN;

@RestController
@Tag(name = OpenApiTagName.USERS)
@AllArgsConstructor
@SecurityRequirement(name = BEARER_TOKEN)
public class GetCurrentUserController {
    private final UserDTOMapper mapper;

    @Operation(summary = "Get user by access token")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The user account",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResponseDTO.class)
            )
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
    })
    @GetMapping("/api/users/me")
    public ResponseEntity<UserResponseDTO> getUserByAccessToken(@AuthenticationPrincipal(errorOnInvalidType = true) User user) {
        return ResponseEntity.ok(mapper.toUserResponse(user));
    }
}
