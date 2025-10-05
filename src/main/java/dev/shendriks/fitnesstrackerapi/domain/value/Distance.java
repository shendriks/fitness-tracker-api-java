package dev.shendriks.fitnesstrackerapi.domain.value;

import lombok.EqualsAndHashCode;

/**
 * Value object representing a distance measured in meters.
 *
 * <p>Provides factory methods and simple arithmetic operations.</p>
 */
@EqualsAndHashCode
public class Distance {
    private Double distanceInMeters;

    private Distance(Double distanceInMeters) {
        this.distanceInMeters = distanceInMeters;
    }

    /**
     * Creates a distance of 0 meters.
     */
    public static Distance zero() {
        return new Distance(0.0);
    }

    /**
     * Factory method to create a distance from meters.
     * @param distance distance in meters
     * @return new Distance instance
     */
    public static Distance ofMeters(Double distance) {
        return new Distance(distance);
    }

    /**
     * Returns the distance value in meters.
     */
    public Double toMeters() {
        return distanceInMeters;
    }

    /**
     * Adds another distance to this instance (in-place).
     * @param distance distance to add
     */
    public void add(Distance distance) {
        distanceInMeters += distance.distanceInMeters;
    }

    /**
     * Adds the given meters to this instance (in-place).
     * @param distance meters to add
     */
    public void addMeters(Double distance) {
        distanceInMeters += distance;
    }
}
