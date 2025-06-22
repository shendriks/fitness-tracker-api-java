package dev.shendriks.fitnesstrackerapi.ratelimiting;

import dev.shendriks.fitnesstrackerapi.error.ApiException;
import org.springframework.http.HttpStatus;

public class RateLimitExceededException extends ApiException {
    public RateLimitExceededException() {
        super(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
    }
}
