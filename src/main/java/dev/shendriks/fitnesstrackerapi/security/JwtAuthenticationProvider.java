package dev.shendriks.fitnesstrackerapi.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final UserRepository repository;

    public JwtAuthenticationProvider(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        if (!(authentication instanceof JwtAuthentication)) {
            throw new IllegalArgumentException("Unsupported authentication type: " + authentication.getClass().getName());
        }

        String token = authentication.getCredentials().toString();

        DecodedJWT decodedJWT;
        try {
            Algorithm algorithm = Algorithm.HMAC512("secret");
            JWTVerifier verifier = JWT
                .require(algorithm)
                .withIssuer("fitness-tracker-api")
                .build();

            decodedJWT = verifier.verify(token);
        } catch (JWTVerificationException exception) {
            throw new BadCredentialsException("Invalid access token: " + exception.getMessage());
        }

        String userId = decodedJWT.getSubject();

        User user = repository
            .findByUlid(userId)
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