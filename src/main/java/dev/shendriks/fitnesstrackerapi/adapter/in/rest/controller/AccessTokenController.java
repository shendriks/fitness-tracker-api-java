package dev.shendriks.fitnesstrackerapi.adapter.in.rest.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import dev.shendriks.fitnesstrackerapi.adapter.in.rest.dto.AccessTokenResponseDTO;
import dev.shendriks.fitnesstrackerapi.domain.entity.UserAdapter;
import dev.shendriks.fitnesstrackerapi.infrastructure.openapi.OpenApiTagName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Duration;
import java.time.Instant;

import static dev.shendriks.fitnesstrackerapi.infrastructure.security.SecurityRequirementName.BASIC_AUTH;

@Controller
@Tag(name = OpenApiTagName.ACCESS_TOKENS, description = "An access token is needed to access secured endpoints")
@SecurityRequirement(name = BASIC_AUTH)
public class AccessTokenController {
    private final String jwtSecret;
    private final String jwtIssuer;
    private final long jwtExpiration;

    public AccessTokenController(
        @Value("${jwt.secret}") String jwtSecret,
        @Value("${jwt.issuer}") String jwtIssuer,
        @Value("${jwt.expiration}") long jwtExpiration
    ) {
        this.jwtSecret = jwtSecret;
        this.jwtIssuer = jwtIssuer;
        this.jwtExpiration = jwtExpiration;
    }

    @Operation(summary = "Get an access token")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The access token",
            content = @Content(schema = @Schema(implementation = AccessTokenResponseDTO.class))
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Not authorized to access the access token", content = @Content),
        @ApiResponse(responseCode = "500", description = "Access token creation failed", content = @Content)
    })
    @GetMapping("/api/access-token")
    public ResponseEntity<AccessTokenResponseDTO> getAccessToken(
        @AuthenticationPrincipal(errorOnInvalidType = true)
        UserAdapter userAdapter
    ) {
        try {
            String token = JWT.create()
                .withIssuer(jwtIssuer)
                .withSubject(userAdapter.user().ulid().value())
                .withExpiresAt(Instant.now().plus(Duration.ofSeconds(jwtExpiration)))
                .sign(Algorithm.HMAC512(jwtSecret));
            return ResponseEntity.ok(new AccessTokenResponseDTO(token));
        } catch (JWTCreationException exception) {
            throw new RuntimeException(exception);
        }
    }
}

