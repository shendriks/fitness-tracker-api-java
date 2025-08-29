package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Duration {
    private final double durationInSeconds;

    private Duration(double durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public static Duration zero() {
        return new Duration(0);
    }

    public static Duration ofSeconds(double duration) {
        return new Duration(duration);
    }

    public double toSeconds() {
        return durationInSeconds;
    }
}
