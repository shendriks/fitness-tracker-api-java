package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller.user;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.ApiErrorResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.user.UserResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.user.UserUpdateRequestDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper.UserDTOMapper;
import dev.shendriks.fitnesstrackerapi.application.port.in.user.UpdateUserUseCase;
import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.value.UserUpdateData;
import dev.shendriks.fitnesstrackerapi.infrastructure.openapi.OpenApiTagName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static dev.shendriks.fitnesstrackerapi.infrastructure.security.SecurityRequirementName.BEARER_TOKEN;

@RestController
@Tag(name = OpenApiTagName.USERS)
@SecurityRequirement(name = BEARER_TOKEN)
@AllArgsConstructor
public class PatchUserController {
    private final UpdateUserUseCase useCase;
    private final UserDTOMapper mapper;

    @Operation(summary = "Update user")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The updated user account",
            content = @Content(schema = @Schema(implementation = UserResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid data",
            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))
        )
    })
    @PatchMapping("/api/users/me")
    public ResponseEntity<UserResponseDTO> updateUser(
        @AuthenticationPrincipal(errorOnInvalidType = true)
        User user,
        @Valid
        @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "The user account update",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserUpdateRequestDTO.class)
            )
        )
        UserUpdateRequestDTO request
    ) {
        UserUpdateData userUpdateData = mapper.toUserUpdateData(request);
        user = useCase.updateUser(user.id(), userUpdateData);
        UserResponseDTO userResponseDto = mapper.toUserResponse(user);
        return ResponseEntity.ok(userResponseDto);
    }
}
