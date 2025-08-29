package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Speed {
    private final Double speedInMetersPerSecond;

    private Speed(Double speedInMetersPerSecond) {
        this.speedInMetersPerSecond = speedInMetersPerSecond;
    }

    public static Speed ofMetersPerSecond(Double speed) {
        return new Speed(speed);
    }

    public static Speed zero() {
        return new Speed(0.0);
    }

    public Double toMetersPerSecond() {
        return speedInMetersPerSecond;
    }

    public Pace toPace() {
        return speedInMetersPerSecond > 0
            ? Pace.ofSecondsPerKilometer(1000.0 / speedInMetersPerSecond)
            : Pace.zero();
    }
}
