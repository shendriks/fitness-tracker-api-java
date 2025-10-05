package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.EqualsAndHashCode;

/**
 * Value object representing a duration measured in seconds.
 */
@EqualsAndHashCode
public class Duration {
    private Long durationInSeconds;

    private Duration(Long durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    /**
     * Creates a zero-length duration.
     */
    public static Duration zero() {
        return new Duration(0L);
    }

    /**
     * Factory to create a duration from seconds.
     * @param duration duration in seconds
     * @return new Duration instance
     */
    public static Duration ofSeconds(Long duration) {
        return new Duration(duration);
    }

    /**
     * Factory to create a domain Duration from a java.time.Duration.
     * @param duration Java Duration
     * @return new Duration instance
     */
    public static Duration ofJavaDuration(java.time.Duration duration) {
        return new Duration(duration.getSeconds());
    }

    /**
     * Returns the duration value in seconds.
     */
    public Long toSeconds() {
        return durationInSeconds;
    }

    /**
     * Adds another duration to this instance (in-place).
     * @param duration duration to add
     */
    public void add(Duration duration) {
        this.durationInSeconds += duration.durationInSeconds;
    }
}
