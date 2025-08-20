package dev.shendriks.fitnesstrackerapi.application.service.gpx.distance;

import io.jenetics.jpx.WayPoint;

public interface DistanceCalculator {
    double calculateDistance(WayPoint p1, WayPoint p2);
}
