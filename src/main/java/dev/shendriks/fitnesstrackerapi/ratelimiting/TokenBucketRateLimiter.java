package dev.shendriks.fitnesstrackerapi.ratelimiting;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Token bucket implementation of the RateLimiter interface.
 * Each client has a bucket with a maximum number of tokens.
 * Tokens are consumed for each request and replenished at a fixed rate.
 */
@Component
public class TokenBucketRateLimiter implements RateLimiter {
    private static final int DEFAULT_MAX_TOKENS = 1;
    private final int maxTokens;
    private final ConcurrentHashMap<String, AtomicInteger> tokenBuckets = new ConcurrentHashMap<>();

    /**
     * Creates a new TokenBucketRateLimiter with the specified maximum tokens.
     *
     * @param maxTokens The maximum number of tokens per bucket
     */
    public TokenBucketRateLimiter(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    /**
     * Default constructor with a default maximum token value of 1.
     */
    public TokenBucketRateLimiter() {
        this(DEFAULT_MAX_TOKENS);
    }

    /**
     * Checks if a request is allowed based on the token bucket rate limiting strategy.
     * Each request consumes one token from the client's bucket. If no tokens are available,
     * the request is rate limited.
     *
     * @param key The identifier for the client/user making the request
     * @return true if the request is allowed (token available), false if it should be rate limited
     */
    @Override
    public boolean tryConsume(String key) {
        synchronized (tokenBuckets) {
            tokenBuckets.putIfAbsent(key, new AtomicInteger(maxTokens));
            if (tokenBuckets.get(key).get() <= 0) {
                return false;
            }
            tokenBuckets.get(key).decrementAndGet();
            return true;
        }
    }

    /**
     * Replenishes all token buckets to their maximum capacity.
     * This method is automatically called every second by the Spring scheduler
     * to provide a constant rate of token replenishment.
     */
    @Scheduled(fixedRate = 1000)
    private void reset() {
        synchronized (tokenBuckets) {
            tokenBuckets.values().forEach(counter -> counter.set(maxTokens));
        }
    }
}
