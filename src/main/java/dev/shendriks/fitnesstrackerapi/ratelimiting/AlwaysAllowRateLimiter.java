package dev.shendriks.fitnesstrackerapi.ratelimiting;

import org.springframework.stereotype.Component;

/**
 * A rate limiter implementation that always allows all requests.
 * This implementation can be used as a no-op rate limiter when rate limiting
 * needs to be disabled without changing the application logic.
 */
@Component
public class AlwaysAllowRateLimiter implements RateLimiter {
    /**
     * Always allows the request regardless of the key or request frequency.
     * 
     * @param key The identifier for the client/user making the request (ignored in this implementation)
     * @return Always returns true, indicating that the request is allowed
     */
    @Override
    public boolean tryConsume(String key) {
        return true;
    }
}
