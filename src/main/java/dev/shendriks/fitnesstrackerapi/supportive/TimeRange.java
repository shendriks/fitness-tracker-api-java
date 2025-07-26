package dev.shendriks.fitnesstrackerapi.supportive;

import java.time.Instant;

public record TimeRange(Instant from, Instant to) {
    public static TimeRange of(Instant from, Instant to) {
        return new TimeRange(from, to);
    }
}
