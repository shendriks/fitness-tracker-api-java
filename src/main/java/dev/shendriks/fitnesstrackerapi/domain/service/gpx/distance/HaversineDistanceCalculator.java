package dev.shendriks.fitnesstrackerapi.domain.service.gpx.distance;

import dev.shendriks.fitnesstrackerapi.domain.value.Distance;
import io.jenetics.jpx.WayPoint;
import org.springframework.stereotype.Component;

@Component
public class HaversineDistanceCalculator implements DistanceCalculator {
    public static final int EARTH_RADIUS_IN_METERS = 6371000;

    @Override
    public Distance calculateDistance(WayPoint p1, WayPoint p2) {
        return calculateDistance(
            p1.getLatitude().doubleValue(),
            p1.getLongitude().doubleValue(),
            p2.getLatitude().doubleValue(),
            p2.getLongitude().doubleValue()
        );
    }

    private Distance calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Distance.ofMeters(EARTH_RADIUS_IN_METERS * c);
    }
}