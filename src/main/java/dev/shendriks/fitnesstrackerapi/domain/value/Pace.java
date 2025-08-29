package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Pace {
    private final Double paceInSecondsPerKilometer;

    private Pace(Double paceInSecondsPerKilometer) {
        this.paceInSecondsPerKilometer = paceInSecondsPerKilometer;
    }

    public static Pace ofSecondsPerKilometer(Double pace) {
        return new Pace(pace);
    }

    public static Pace zero() {
        return new Pace(0.0);
    }

    public Double toSecondsPerKilometer() {
        return paceInSecondsPerKilometer;
    }
}
