package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Duration {
    @EqualsAndHashCode.Include
    private Double durationInSeconds;

    private Duration(Double durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public static Duration zero() {
        return new Duration(0.0);
    }

    public static Duration ofSeconds(Double duration) {
        return new Duration(duration);
    }

    public Double toSeconds() {
        return durationInSeconds;
    }

    public void add(Duration duration) {
        this.durationInSeconds += duration.durationInSeconds;
    }
}
