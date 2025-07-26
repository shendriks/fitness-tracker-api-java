package dev.shendriks.fitnesstrackerapi.ratelimiting;

public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException() {
        super("Rate limit exceeded. Try again later.");
    }
}
