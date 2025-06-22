package dev.shendriks.fitnesstrackerapi.developer.controller;

import dev.shendriks.fitnesstrackerapi.developer.dto.DeveloperResponse;
import dev.shendriks.fitnesstrackerapi.developer.dto.DeveloperSignupRequest;
import dev.shendriks.fitnesstrackerapi.developer.exception.DeveloperNotFoundException;
import dev.shendriks.fitnesstrackerapi.developer.exception.EmailAlreadyRegisteredException;
import dev.shendriks.fitnesstrackerapi.developer.service.DeveloperService;
import dev.shendriks.fitnesstrackerapi.error.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/developers")
public class DeveloperController {
    private final DeveloperService service;

    public DeveloperController(DeveloperService service) {
        this.service = service;
    }

    @Operation(summary = "Register a developer")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Developer account created",
            content = @Content,
            headers = {
                @Header(
                    name = "Location",
                    description = "The URI of the developer account",
                    schema = @Schema(type = "string")
                )
            }
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid data", 
            content = @Content(schema = @Schema(implementation = ApiError.class))
        )
    })
    @PostMapping("/signup")
    public ResponseEntity<Void> registerDeveloper(
        @Valid 
        @RequestBody
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "The developer account to create",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DeveloperSignupRequest.class),
                examples = @ExampleObject(value = """
                        {
                            "email": "foo@bar.baz",
                            "password": "Sup3rS3cr3tPa$$w0rd!"
                        }
                    """))
        )    
        DeveloperSignupRequest request
    ) {
        DeveloperResponse developer = service.findDeveloperByEmail(request.email());
        if (developer != null) {
            throw new EmailAlreadyRegisteredException();
        }

        DeveloperResponse developerResponse = this.service.save(request);
        URI location = URI.create("/api/developers/" + developerResponse.id());
        
        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "Get a developer by id")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The developer account including the list of applications this developer has registered",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = DeveloperResponse.class)
            )
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Not authorized to access the developer account", content = @Content),
        @ApiResponse(responseCode = "404", description = "The developer account was not found", content = @Content)
    })
    @GetMapping("/{id}")
    @SecurityRequirement(name = "Basic Auth")
    public ResponseEntity<DeveloperResponse> getDeveloper(
        @AuthenticationPrincipal UserDetails details, 
        @PathVariable
        @Parameter(
            name = "id",
            description = "The developer id; must match the id of the authenticated developer",
            required = true
        )    
        String id
    ) {
        DeveloperResponse developerResponse = this.service.findDeveloperById(id);
        if (developerResponse == null) {
            throw new DeveloperNotFoundException();
        }

        if (details == null || !details.getUsername().equals(developerResponse.email())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok().body(developerResponse);
    }
}
