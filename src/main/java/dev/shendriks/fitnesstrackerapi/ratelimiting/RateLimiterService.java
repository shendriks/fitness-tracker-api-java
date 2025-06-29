package dev.shendriks.fitnesstrackerapi.ratelimiting;

import dev.shendriks.fitnesstrackerapi.domain.application.entity.Application;
import dev.shendriks.fitnesstrackerapi.domain.user.enums.AccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;

/**
 * Service that manages rate limiting for the application.
 */
@Service
public class RateLimiterService {

    private final Map<AccountType, RateLimiter> rateLimiters;

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
        rateLimiters = new EnumMap<>(AccountType.class);
        rateLimiters.put(AccountType.BASIC, tokenBucketRateLimiter);
        rateLimiters.put(AccountType.PREMIUM, alwaysAllowRateLimiter);
    }

    /**
     * Checks if a request from the given application should be rate limited.
     *
     * @param application The application making the request
     * @return false if rate limited, true otherwise
     */
    public boolean isRequestAllowed(Application application) {
        RateLimiter rateLimiter = rateLimiters.get(application.getAccountType());
        return rateLimiter == null || rateLimiter.tryConsume(application.getApiKey());
    }
}
