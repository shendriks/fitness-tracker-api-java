package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Duration {
    private Long durationInSeconds;

    private Duration(Long durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public static Duration zero() {
        return new Duration(0L);
    }

    public static Duration ofSeconds(Long duration) {
        return new Duration(duration);
    }

    public static Duration ofJavaDuration(java.time.Duration duration) {
        return new Duration(duration.getSeconds());
    }

    public Long toSeconds() {
        return durationInSeconds;
    }

    public void add(Duration duration) {
        this.durationInSeconds += duration.durationInSeconds;
    }
}
