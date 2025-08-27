package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller.user;

import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.ApiErrorResponseDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.user.UserSignupRequestDTO;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.mapper.UserDTOMapper;
import dev.shendriks.fitnesstrackerapi.application.port.in.user.SignUpUseCase;
import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.value.UserSignupData;
import dev.shendriks.fitnesstrackerapi.infrastructure.openapi.OpenApiTagName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@Tag(name = OpenApiTagName.USERS)
@AllArgsConstructor
public class SignUpUserController {
    private final SignUpUseCase signUpUseCase;
    private final UserDTOMapper mapper;

    @Operation(summary = "Register a user")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "User account created",
            content = @Content,
            headers = {@Header(
                name = "Location",
                description = "The URI of the user account",
                schema = @Schema(type = "string")
            )}
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid data",
            content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class))
        )
    })
    @PostMapping("/api/users/signup")
    public ResponseEntity<Void> registerUser(
        @Valid
        @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "The user account to create",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserSignupRequestDTO.class),
                examples = @ExampleObject(value = """
                        {
                            "name": "John Doe",
                            "email": "foo@bar.baz",
                            "password": "Sup3rS3cr3tPa$$w0rd!",
                            "accountType": "basic"
                        }
                    """))
        )
        UserSignupRequestDTO request
    ) {
        UserSignupData userSignupData = mapper.toUserSignUpData(request);
        User user = signUpUseCase.signUp(userSignupData);
        URI location = URI.create("/api/users/" + user.ulid().value());
        return ResponseEntity.created(location).build();
    }
}
