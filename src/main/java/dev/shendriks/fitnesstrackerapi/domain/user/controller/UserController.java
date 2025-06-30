package dev.shendriks.fitnesstrackerapi.domain.user.controller;

import dev.shendriks.fitnesstrackerapi.domain.user.dto.UserResponse;
import dev.shendriks.fitnesstrackerapi.domain.user.dto.UserSignupRequest;
import dev.shendriks.fitnesstrackerapi.domain.user.exception.UserNotFoundException;
import dev.shendriks.fitnesstrackerapi.domain.user.exception.EmailAlreadyRegisteredException;
import dev.shendriks.fitnesstrackerapi.domain.user.service.UserService;
import dev.shendriks.fitnesstrackerapi.error.ApiError;
import dev.shendriks.fitnesstrackerapi.openapi.OpenApiTagName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static dev.shendriks.fitnesstrackerapi.security.SecurityRequirementName.BASIC_AUTH;

@RestController
@RequestMapping("/api/users")
@Tag(name = OpenApiTagName.USERS, description = "A user can sign up, read and write activities")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

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
            content = @Content(schema = @Schema(implementation = ApiError.class))
        )
    })
    @PostMapping("/signup")
    public ResponseEntity<Void> registerUser(
        @Valid
        @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "The user account to create",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserSignupRequest.class),
                examples = @ExampleObject(value = """
                        {
                            "email": "foo@bar.baz",
                            "password": "Sup3rS3cr3tPa$$w0rd!",
                            "accountType": "basic"
                        }
                    """))
        )
        UserSignupRequest request
    ) {
        String email = request.email().toLowerCase().trim();
        UserResponse user = service.findDeveloperByEmail(email);
        if (user != null) {
            throw new EmailAlreadyRegisteredException();
        }

        UserResponse userResponse = this.service.save(request);
        URI location = URI.create("/api/users/" + userResponse.id());

        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Get a developer by id")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The developer account including the list of applications this developer has registered",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResponse.class)
            )
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Not authorized to access the developer account", content = @Content),
        @ApiResponse(responseCode = "404", description = "The developer account was not found", content = @Content)
    })
    @GetMapping("/{id}")
    @SecurityRequirement(name = BASIC_AUTH)
    public ResponseEntity<UserResponse> getDeveloper(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable
        @Parameter(
            name = "id",
            description = "The developer id; must match the id of the authenticated developer",
            required = true
        )
        String id
    ) {
        UserResponse userResponse = this.service.findDeveloperById(id);
        if (userResponse == null) {
            throw new UserNotFoundException();
        }

        if (userDetails == null || !userDetails.getUsername().equals(userResponse.email())) {
            throw new AccessDeniedException("Not authorized to access the developer account");
        }

        return ResponseEntity.ok().body(userResponse);
    }
}
