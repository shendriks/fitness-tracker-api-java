package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Distance {
    private final double distanceInMeters;

    private Distance(double distanceInMeters) {
        this.distanceInMeters = distanceInMeters;
    }

    public static Distance zero() {
        return new Distance(0);
    }

    public static Distance ofMeters(double distance) {
        return new Distance(distance);
    }

    public double toMeters() {
        return distanceInMeters;
    }
}
