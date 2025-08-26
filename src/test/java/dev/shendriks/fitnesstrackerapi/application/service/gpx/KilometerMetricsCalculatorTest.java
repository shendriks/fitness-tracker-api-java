package dev.shendriks.fitnesstrackerapi.application.service.gpx;

import dev.shendriks.fitnesstrackerapi.application.exception.WayPointsNotSortedException;
import dev.shendriks.fitnesstrackerapi.application.service.gpx.distance.DistanceCalculator;
import dev.shendriks.fitnesstrackerapi.domain.value.KilometerMetrics;
import io.jenetics.jpx.WayPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KilometerMetricsCalculatorTest {
    public static final double EPSILON = 0.000_000_001;
    @Mock
    DistanceCalculator distanceCalculator;
    private KilometerMetricsCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new KilometerMetricsCalculator(distanceCalculator);
    }

    @Test
    void calculateKilometerMetrics_withTooFewPoints_returnsEmptyLists() {
        KilometerMetrics actualMetrics = calculator.calculateKilometerMetrics(List.of());

        assertTrue(actualMetrics.speeds().isEmpty());
        assertTrue(actualMetrics.paces().isEmpty());

        actualMetrics = calculator.calculateKilometerMetrics(List.of(
            WayPoint.builder().lat(0).lon(0).time(Instant.parse("2025-08-22T10:00:00Z")).build()
        ));

        assertTrue(actualMetrics.speeds().isEmpty());
        assertTrue(actualMetrics.paces().isEmpty());
    }

    @Test
    void calculateKilometerMetrics_createsFullKmSegment_andSignificantPartial() {
        Instant t0 = Instant.parse("2025-08-22T10:00:00Z");
        Instant t1 = t0.plusSeconds(10);
        Instant t2 = t0.plusSeconds(40); // first segment end
        Instant t3 = t0.plusSeconds(70); // leftover end
        WayPoint wayPoint0 = WayPoint.builder().lat(0.0).lon(0.0).time(t0).build();
        WayPoint wayPoint1 = WayPoint.builder().lat(0.0).lon(0.1).time(t1).build();
        WayPoint wayPoint2 = WayPoint.builder().lat(0.0).lon(0.2).time(t2).build();
        WayPoint wayPoint3 = WayPoint.builder().lat(0.0).lon(0.3).time(t3).build();
        List<WayPoint> wayPoints = List.of(wayPoint0, wayPoint1, wayPoint2, wayPoint3);

        // wayPoint0 -> wayPoint1 = 400m
        // wayPoint1 -> wayPoint2 = 700m (total 1100m -> creates full segment)
        // wayPoint2 -> wayPoint3 = 150m leftover
        when(distanceCalculator.calculateDistance(wayPoint0, wayPoint1)).thenReturn(400.0);
        when(distanceCalculator.calculateDistance(wayPoint1, wayPoint2)).thenReturn(700.0);
        when(distanceCalculator.calculateDistance(wayPoint2, wayPoint3)).thenReturn(150.0);

        KilometerMetrics actualMetrics = calculator.calculateKilometerMetrics(wayPoints);

        assertEquals(2, actualMetrics.speeds().size(), "Expected two speed entries");
        assertEquals(2, actualMetrics.paces().size(), "Expected two pace entries");

        // Segment 1: distance 1100m over 40s
        double expectedSpeed1 = 1100.0 / 40.0;
        double expectedPace1 = 1000.0 / expectedSpeed1;
        assertEquals(expectedSpeed1, actualMetrics.speeds().getFirst(), EPSILON);
        assertEquals(expectedPace1, actualMetrics.paces().getFirst(), EPSILON);

        // Segment 2 (partial): distance 150m over 30s
        double expectedSpeed2 = 150.0 / 30.0;
        double expectedPace2 = 1000.0 / expectedSpeed2;
        assertEquals(expectedSpeed2, actualMetrics.speeds().get(1), EPSILON);
        assertEquals(expectedPace2, actualMetrics.paces().get(1), EPSILON);
    }

    @Test
    void calculateKilometerMetrics_skipsPointsWithoutTime_andUsesLastTimedPointForDistance() {
        Instant t0 = Instant.parse("2025-08-22T10:00:00Z");
        Instant t2 = t0.plusSeconds(60);
        WayPoint wayPoint0 = WayPoint.builder().lat(0.0).lon(0.0).time(t0).build();
        WayPoint wayPoint1 = WayPoint.builder().lat(0.0).lon(0.001).build(); // no time
        WayPoint wayPoint2 = WayPoint.builder().lat(0.0).lon(0.002).time(t2).build();
        List<WayPoint> wayPoints = List.of(wayPoint0, wayPoint1, wayPoint2);

        when(distanceCalculator.calculateDistance(wayPoint0, wayPoint2)).thenReturn(1000.0);

        KilometerMetrics actualMetrics = calculator.calculateKilometerMetrics(wayPoints);

        assertEquals(1, actualMetrics.speeds().size(), "Expected one speed entry");
        assertEquals(1, actualMetrics.paces().size(), "Expected one pace entry");

        double expectedSpeed = 1000.0 / 60.0;
        double expectedPace = 1000.0 / expectedSpeed;
        assertEquals(expectedSpeed, actualMetrics.speeds().getFirst(), EPSILON);
        assertEquals(expectedPace, actualMetrics.paces().getFirst(), EPSILON);
    }

    @Test
    void calculateKilometerMetrics_exactlyOneKmBoundary_addsSegment_andInsignificantLeftoverIgnored() {
        Instant t0 = Instant.parse("2025-08-22T10:00:00Z");
        Instant t1 = t0.plusSeconds(20);
        Instant t2 = t0.plusSeconds(50);
        WayPoint wayPoint0 = WayPoint.builder().lat(0.0).lon(0.0).time(t0).build();
        WayPoint wayPoint1 = WayPoint.builder().lat(0.0).lon(0.001).time(t1).build();
        WayPoint wayPoint2 = WayPoint.builder().lat(0.0).lon(0.002).time(t2).build();
        List<WayPoint> wayPoints = List.of(wayPoint0, wayPoint1, wayPoint2);

        when(distanceCalculator.calculateDistance(wayPoint0, wayPoint1)).thenReturn(600.0);
        when(distanceCalculator.calculateDistance(wayPoint1, wayPoint2)).thenReturn(400.0);

        KilometerMetrics actualMetrics = calculator.calculateKilometerMetrics(wayPoints);

        assertEquals(1, actualMetrics.speeds().size(), "Expected one speed entry");
        assertEquals(1, actualMetrics.paces().size(), "Expected one pace entry");

        double expectedSpeed = 1000.0 / 50.0;
        double expectedPace = 1000.0 / expectedSpeed;
        assertEquals(expectedSpeed, actualMetrics.speeds().getFirst(), EPSILON);
        assertEquals(expectedPace, actualMetrics.paces().getFirst(), EPSILON);
    }

    @Test
    void calculateKilometerMetrics_zeroDuration_resultsInZeroSpeedAndPace() {
        Instant t0 = Instant.parse("2025-08-22T10:00:00Z");
        WayPoint wayPoint0 = WayPoint.builder().lat(0.0).lon(0.0).time(t0).build();
        WayPoint wayPoint1 = WayPoint.builder().lat(0.0).lon(0.001).time(t0).build();
        List<WayPoint> wayPoints = List.of(wayPoint0, wayPoint1);

        when(distanceCalculator.calculateDistance(wayPoint0, wayPoint1)).thenReturn(1000.0);

        KilometerMetrics actualMetrics = calculator.calculateKilometerMetrics(wayPoints);

        assertEquals(1, actualMetrics.speeds().size(), "Expected one speed entry");
        assertEquals(1, actualMetrics.paces().size(), "Expected one pace entry");
        assertEquals(0.0, actualMetrics.speeds().getFirst(), "Expected zero speed");
        assertEquals(0.0, actualMetrics.paces().getFirst(), "Expected zero pace");
    }
    
    @Test
    void calculateKilometerMetrics_wayPointsNotSortedByTime_throwsException() {
        Instant t0 = Instant.parse("2025-08-22T10:00:00Z");
        Instant t1 = t0.minusSeconds(1);
        
        WayPoint wayPoint0 = WayPoint.builder().lat(0.0).lon(0.0).time(t0).build();
        WayPoint wayPoint1 = WayPoint.builder().lat(0.0).lon(0.001).time(t1).build();
        List<WayPoint> wayPoints = List.of(wayPoint0, wayPoint1);

        assertThrows(WayPointsNotSortedException.class, () -> calculator.calculateKilometerMetrics(wayPoints));
    }
}
