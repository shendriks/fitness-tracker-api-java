package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.EqualsAndHashCode;

/**
 * Value object representing speed measured in meters per second.
 */
@EqualsAndHashCode
public class Speed {
    private final Double speedInMetersPerSecond;

    private Speed(Double speedInMetersPerSecond) {
        this.speedInMetersPerSecond = speedInMetersPerSecond;
    }

    /**
     * Factory to create a speed from meters per second.
     * @param speed meters per second
     * @return new Speed instance
     */
    public static Speed ofMetersPerSecond(Double speed) {
        return new Speed(speed);
    }

    /**
     * Creates a speed of 0 m/s.
     */
    public static Speed zero() {
        return new Speed(0.0);
    }

    /**
     * Returns the speed in meters per second.
     */
    public Double toMetersPerSecond() {
        return speedInMetersPerSecond;
    }

    /**
     * Converts this speed to running pace (seconds per kilometer).
     * @return Pace value corresponding to this speed
     */
    public Pace toPace() {
        return speedInMetersPerSecond > 0
            ? Pace.ofSecondsPerKilometer(1000.0 / speedInMetersPerSecond)
            : Pace.zero();
    }
}
