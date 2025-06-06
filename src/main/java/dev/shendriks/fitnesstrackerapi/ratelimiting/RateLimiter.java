package dev.shendriks.fitnesstrackerapi.ratelimiting;

/**
 * Interface for rate limiting strategies.
 */
public interface RateLimiter {

    /**
     * Checks if a request is allowed based on the rate limiting strategy.
     * 
     * @param key The identifier for the client/user making the request
     * @return true if the request is allowed, false if not
     */
    boolean tryConsume(String key);
}
