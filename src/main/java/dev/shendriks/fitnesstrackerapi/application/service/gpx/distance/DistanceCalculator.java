package dev.shendriks.fitnesstrackerapi.application.service.gpx.distance;

import dev.shendriks.fitnesstrackerapi.domain.value.Distance;
import io.jenetics.jpx.WayPoint;

public interface DistanceCalculator {
    Distance calculateDistance(WayPoint p1, WayPoint p2);
}
