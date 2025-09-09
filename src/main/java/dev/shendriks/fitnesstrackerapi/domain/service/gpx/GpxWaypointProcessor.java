package dev.shendriks.fitnesstrackerapi.domain.service.gpx;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GpxWaypointProcessor {
    public List<WayPoint> getAllWayPointsOrderedByTime(GPX gpx) {
        return gpx
            .tracks()
            .flatMap(Track::segments)
            .flatMap(TrackSegment::points)
            .filter(point -> point.getTime().isPresent())
            .sorted(Comparator.comparing(p -> p.getTime().orElse(Instant.MIN)))
            .collect(Collectors.toList());
    }
}