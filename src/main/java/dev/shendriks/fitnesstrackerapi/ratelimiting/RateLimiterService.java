package dev.shendriks.fitnesstrackerapi.ratelimiting;

import dev.shendriks.fitnesstrackerapi.domain.user.entity.User;
import dev.shendriks.fitnesstrackerapi.domain.user.enums.AccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;

/**
 * Service that manages rate limiting for the user.
 */
@Service
public class RateLimiterService {

    private final Map<AccountType, RateLimiter> rateLimiters;

    /**
     * Creates a new RateLimiterService with the specified rate limiters.
     *
     * @param tokenBucketRateLimiter The token bucket rate limiter for BASIC user accounts
     * @param alwaysAllowRateLimiter The no-op rate limiter for PREMIUM user accounts
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
     * Checks if a request from the given user should be rate limited.
     *
     * @param user The user making the request
     * @return false if rate limited, true otherwise
     */
    public boolean isRequestAllowed(User user) {
        RateLimiter rateLimiter = rateLimiters.get(user.getAccountType());
        return rateLimiter == null || rateLimiter.tryConsume(user.getUlid());
    }
}
