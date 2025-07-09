package dev.shendriks.fitnesstrackerapi.controller.accesstoken.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import dev.shendriks.fitnesstrackerapi.domain.user.security.UserAdapter;
import dev.shendriks.fitnesstrackerapi.openapi.OpenApiTagName;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Duration;
import java.time.Instant;

import static dev.shendriks.fitnesstrackerapi.security.SecurityRequirementName.BASIC_AUTH;


@Controller
@RequestMapping("/api/access-token")
@Tag(name = OpenApiTagName.ACCESS_TOKENS, description = "An access token is needed to access secured endpoints")
@SecurityRequirement(name = BASIC_AUTH)
public class AccessTokenController {
    @Operation(summary = "Get an access token")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The access token",
            content = @Content
        ),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Not authorized to access the access token", content = @Content)
    })
    @GetMapping
    public ResponseEntity<String> getAccessToken(@AuthenticationPrincipal UserAdapter userAdapter) {
        try {
            Algorithm algorithm = Algorithm.HMAC512("secret");
            String token = JWT.create()
                .withIssuer("fitness-tracker-api")
                .withSubject(userAdapter.getUser().getUlid())
                .withExpiresAt(Instant.now().plus(Duration.ofHours(4)))
                .sign(algorithm);
            return ResponseEntity.ok("{\"token\": \"" + token + "\"}");
        } catch (JWTCreationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
