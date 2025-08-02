package dev.shendriks.fitnesstrackerapi.domain.gpx.util.distance;

import io.jenetics.jpx.WayPoint;

public interface DistanceCalculator {
    double calculateDistance(WayPoint p1, WayPoint p2);
}
