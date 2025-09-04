package dev.shendriks.fitnesstrackerapi.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import dev.shendriks.fitnesstrackerapi.application.port.out.ForAccessingUsers;
import dev.shendriks.fitnesstrackerapi.domain.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.value.UserUlid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final String jwtSecret;
    private final String jwtIssuer;
    private final ForAccessingUsers forAccessingUsers;

    public JwtAuthenticationProvider(
        ForAccessingUsers forAccessingUsers,
        @Value("${jwt.secret}") String jwtSecret,
        @Value("${jwt.issuer}") String jwtIssuer
    ) {
        this.forAccessingUsers = forAccessingUsers;
        this.jwtSecret = jwtSecret;
        this.jwtIssuer = jwtIssuer;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        if (!(authentication instanceof JwtAuthentication)) {
            throw new IllegalArgumentException("Unsupported authentication type: " + authentication.getClass().getName());
        }

        String token = authentication.getCredentials().toString();

        DecodedJWT decodedJWT;
        try {
            JWTVerifier verifier = JWT
                .require(Algorithm.HMAC512(jwtSecret))
                .withIssuer(jwtIssuer)
                .build();

            decodedJWT = verifier.verify(token);
        } catch (JWTVerificationException exception) {
            log.info("Invalid access token: {}", exception.getMessage());
            throw new BadCredentialsException("Invalid access token");
        }

        String userId = decodedJWT.getSubject();

        User user = forAccessingUsers
            .findByUlid(new UserUlid(userId))
            .orElseThrow(() -> new BadCredentialsException("User not found"));

        JwtAuthentication jwtAuthentication = new JwtAuthentication(token);
        jwtAuthentication.setUser(user);
        jwtAuthentication.setAuthenticated(true);
        return jwtAuthentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthentication.class.equals(authentication);
    }
}