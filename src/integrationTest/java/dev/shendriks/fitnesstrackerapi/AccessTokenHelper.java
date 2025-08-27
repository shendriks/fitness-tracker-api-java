package dev.shendriks.fitnesstrackerapi;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class AccessTokenHelper {
    private final String jwtSecret;
    private final String jwtIssuer;
    private final long jwtExpiration;

    public AccessTokenHelper(
        @Value("${jwt.secret}") String jwtSecret,
        @Value("${jwt.issuer}") String jwtIssuer,
        @Value("${jwt.expiration}") long jwtExpiration
    ) {
        this.jwtSecret = jwtSecret;
        this.jwtIssuer = jwtIssuer;
        this.jwtExpiration = jwtExpiration;
    }

    public String getAccessToken(String userUlid) {
        return JWT.create()
            .withIssuer(jwtIssuer)
            .withSubject(userUlid)
            .withExpiresAt(Instant.now().plus(Duration.ofSeconds(jwtExpiration)))
            .sign(Algorithm.HMAC512(jwtSecret));
    }

    public void validateAccessToken(String token) {
        JWTVerifier verifier = JWT
            .require(Algorithm.HMAC512(jwtSecret))
            .withIssuer(jwtIssuer)
            .build();

        verifier.verify(token);
    }
}
