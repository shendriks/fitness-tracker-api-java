package dev.shendriks.fitnesstrackerapi.infrastructure.ratelimiting;

public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException() {
        super("Rate limit exceeded. Try again later.");
    }
}
