package dev.shendriks.fitnesstrackerapi.domain.application.controller;

import dev.shendriks.fitnesstrackerapi.domain.application.dto.ApplicationRegisterRequest;
import dev.shendriks.fitnesstrackerapi.domain.application.dto.ApplicationRegisterResponse;
import dev.shendriks.fitnesstrackerapi.domain.application.service.ApplicationService;
import dev.shendriks.fitnesstrackerapi.error.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static dev.shendriks.fitnesstrackerapi.security.SecurityRequirementName.BASIC_AUTH;


@Controller
@RequestMapping("/api/applications")
@Tag(name = "Application", description = "An application is an API client")
@SecurityRequirement(name = BASIC_AUTH)
public class ApplicationController {
    private final ApplicationService service;

    public ApplicationController(ApplicationService service) {
        this.service = service;
    }

    @Operation(summary = "Register an application")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Application registered",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApplicationRegisterResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid data",
            content = @Content(schema = @Schema(implementation = ApiError.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<ApplicationRegisterResponse> registerApplication(
        @AuthenticationPrincipal UserDetails details,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "The application to register",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApplicationRegisterRequest.class),
                examples = {@ExampleObject(value = """
                    {
                        "name": "My App",
                        "description": "My shiny new application",
                        "category": "basic"
                    }
                    """)}
            )
        )
        @RequestBody @Valid ApplicationRegisterRequest request
    ) {
        ApplicationRegisterResponse response = service.register(request, details);
        return ResponseEntity.ok(response);
    }
}
