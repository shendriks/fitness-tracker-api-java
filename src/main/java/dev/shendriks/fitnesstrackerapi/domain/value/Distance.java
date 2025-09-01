package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Distance {
    private Double distanceInMeters;

    private Distance(Double distanceInMeters) {
        this.distanceInMeters = distanceInMeters;
    }

    public static Distance zero() {
        return new Distance(0.0);
    }

    public static Distance ofMeters(Double distance) {
        return new Distance(distance);
    }

    public Double toMeters() {
        return distanceInMeters;
    }

    public void add(Distance distance) {
        distanceInMeters += distance.distanceInMeters;
    }

    public void addMeters(Double distance) {
        distanceInMeters += distance;
    }
}
