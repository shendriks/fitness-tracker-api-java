package dev.shendriks.fitnesstrackerapi.ratelimiting;

import dev.shendriks.fitnesstrackerapi.application.Application;
import dev.shendriks.fitnesstrackerapi.application.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;

/**
 * Service that manages rate limiting for the application.
 */
@Service
public class RateLimiterService {

    private final Map<Category, RateLimiter> rateLimiters;

    /**
     * Creates a new RateLimiterService with the specified rate limiters.
     *
     * @param tokenBucketRateLimiter The token bucket rate limiter for BASIC applications
     * @param alwaysAllowRateLimiter The no-op rate limiter for PREMIUM applications
     */
    @Autowired
    public RateLimiterService(
        TokenBucketRateLimiter tokenBucketRateLimiter,
        AlwaysAllowRateLimiter alwaysAllowRateLimiter
    ) {
        rateLimiters = new EnumMap<>(Category.class);
        rateLimiters.put(Category.BASIC, tokenBucketRateLimiter);
        rateLimiters.put(Category.PREMIUM, alwaysAllowRateLimiter);
    }

    /**
     * Checks if a request from the given application should be rate limited.
     *
     * @param application The application making the request
     * @return false if rate limited, true otherwise
     */
    public boolean isRequestAllowed(Application application) {
        RateLimiter rateLimiter = rateLimiters.get(application.getCategory());
        return rateLimiter == null || rateLimiter.tryConsume(application.getApiKey());
    }
}
