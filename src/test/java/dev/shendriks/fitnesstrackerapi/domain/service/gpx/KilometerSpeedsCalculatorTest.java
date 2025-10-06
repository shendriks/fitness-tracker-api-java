package dev.shendriks.fitnesstrackerapi.domain.service.gpx;

import dev.shendriks.fitnesstrackerapi.domain.exception.WayPointsNotSortedException;
import dev.shendriks.fitnesstrackerapi.domain.service.gpx.distance.DistanceCalculator;
import dev.shendriks.fitnesstrackerapi.domain.value.Distance;
import dev.shendriks.fitnesstrackerapi.domain.value.Duration;
import dev.shendriks.fitnesstrackerapi.domain.value.Speed;
import dev.shendriks.fitnesstrackerapi.infrastructure.Constant;
import io.jenetics.jpx.WayPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KilometerSpeedsCalculatorTest {
    @Mock
    DistanceCalculator distanceCalculator;
    @Mock
    SpeedCalculator speedCalculator;
    private KilometerSpeedsCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new KilometerSpeedsCalculator(distanceCalculator, speedCalculator);
    }

    @Test
    void calculateKilometerSpeeds_withTooFewPoints_returnsEmptyLists() {
        List<Speed> actualSpeeds = calculator.calculateKilometerSpeeds(List.of());

        assertTrue(actualSpeeds.isEmpty());

        actualSpeeds = calculator.calculateKilometerSpeeds(List.of(
            WayPoint.builder().lat(0).lon(0).time(Instant.parse("2025-08-22T10:00:00Z")).build()
        ));

        assertTrue(actualSpeeds.isEmpty());
    }

    @Test
    void calculateKilometerSpeeds_createsFullKmSegmentAndSignificantPartial() {
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
        when(distanceCalculator.calculateDistance(wayPoint0, wayPoint1)).thenReturn(Distance.ofMeters(400.0));
        when(distanceCalculator.calculateDistance(wayPoint1, wayPoint2)).thenReturn(Distance.ofMeters(700.0));
        when(distanceCalculator.calculateDistance(wayPoint2, wayPoint3)).thenReturn(Distance.ofMeters(150.0));

        when(speedCalculator.calculateSpeed(Distance.ofMeters(1100.0), Duration.ofSeconds(40L)))
            .thenReturn(Speed.ofMetersPerSecond(1100.0 / 40.0));
        when(speedCalculator.calculateSpeed(Distance.ofMeters(150.0), Duration.ofSeconds(30L)))
            .thenReturn(Speed.ofMetersPerSecond(150.0 / 30.0));

        List<Speed> actualMetrics = calculator.calculateKilometerSpeeds(wayPoints);

        assertEquals(2, actualMetrics.size(), "Expected two speed entries");

        // Segment 1: distance 1100m over 40s
        double expectedSpeed1 = 1100.0 / 40.0;
        assertEquals(expectedSpeed1, actualMetrics.getFirst().toMetersPerSecond(), Constant.EPSILON);

        // Segment 2 (partial): distance 150m over 30s
        double expectedSpeed2 = 150.0 / 30.0;
        assertEquals(expectedSpeed2, actualMetrics.get(1).toMetersPerSecond(), Constant.EPSILON);

        verify(speedCalculator, times(1))
            .calculateSpeed(Distance.ofMeters(1100.0), Duration.ofSeconds(40L));
        verify(speedCalculator, times(1))
            .calculateSpeed(Distance.ofMeters(1100.0), Duration.ofSeconds(40L));
        verifyNoMoreInteractions(speedCalculator);
    }

    @Test
    void calculateKilometerSpeeds_skipsPointsWithoutTimeAndUsesLastTimedPointForDistance() {
        Instant t0 = Instant.parse("2025-08-22T10:00:00Z");
        Instant t2 = t0.plusSeconds(60);
        WayPoint wayPoint0 = WayPoint.builder().lat(0.0).lon(0.0).time(t0).build();
        WayPoint wayPoint1 = WayPoint.builder().lat(0.0).lon(0.001).build(); // no time
        WayPoint wayPoint2 = WayPoint.builder().lat(0.0).lon(0.002).time(t2).build();
        List<WayPoint> wayPoints = List.of(wayPoint0, wayPoint1, wayPoint2);

        when(distanceCalculator.calculateDistance(wayPoint0, wayPoint2)).thenReturn(Distance.ofMeters(1000.0));
        when(speedCalculator.calculateSpeed(Distance.ofMeters(1000.0), Duration.ofSeconds(60L)))
            .thenReturn(Speed.ofMetersPerSecond(1000.0 / 60.0));

        List<Speed> actualMetrics = calculator.calculateKilometerSpeeds(wayPoints);

        assertEquals(1, actualMetrics.size(), "Expected one speed entry");

        double expectedSpeed = 1000.0 / 60.0;
        assertEquals(expectedSpeed, actualMetrics.getFirst().toMetersPerSecond(), Constant.EPSILON);

        verify(speedCalculator, times(1))
            .calculateSpeed(Distance.ofMeters(1000.0), Duration.ofSeconds(60L));
        verifyNoMoreInteractions(speedCalculator);
    }

    @Test
    void calculateKilometerSpeeds_withExactlyOneKmBoundary_addsOneSegment() {
        Instant t0 = Instant.parse("2025-08-22T10:00:00Z");
        Instant t1 = t0.plusSeconds(20);
        Instant t2 = t0.plusSeconds(50);
        WayPoint wayPoint0 = WayPoint.builder().lat(0.0).lon(0.0).time(t0).build();
        WayPoint wayPoint1 = WayPoint.builder().lat(0.0).lon(0.001).time(t1).build();
        WayPoint wayPoint2 = WayPoint.builder().lat(0.0).lon(0.002).time(t2).build();
        List<WayPoint> wayPoints = List.of(wayPoint0, wayPoint1, wayPoint2);

        when(distanceCalculator.calculateDistance(wayPoint0, wayPoint1)).thenReturn(Distance.ofMeters(600.0));
        when(distanceCalculator.calculateDistance(wayPoint1, wayPoint2)).thenReturn(Distance.ofMeters(400.0));
        when(speedCalculator.calculateSpeed(Distance.ofMeters(1000.0), Duration.ofSeconds(50L)))
            .thenReturn(Speed.ofMetersPerSecond(1000.0 / 50.0));

        List<Speed> actualMetrics = calculator.calculateKilometerSpeeds(wayPoints);

        assertEquals(1, actualMetrics.size(), "Expected one speed entry");

        double expectedSpeed = 1000.0 / 50.0;
        assertEquals(expectedSpeed, actualMetrics.getFirst().toMetersPerSecond(), Constant.EPSILON);

        verify(speedCalculator, times(1))
            .calculateSpeed(Distance.ofMeters(1000.0), Duration.ofSeconds(50L));
        verifyNoMoreInteractions(speedCalculator);
    }

    @Test
    void calculateKilometerSpeeds_withZeroDuration_resultsInZeroSpeedAndPace() {
        Instant t0 = Instant.parse("2025-08-22T10:00:00Z");
        WayPoint wayPoint0 = WayPoint.builder().lat(0.0).lon(0.0).time(t0).build();
        WayPoint wayPoint1 = WayPoint.builder().lat(0.0).lon(0.001).time(t0).build();
        List<WayPoint> wayPoints = List.of(wayPoint0, wayPoint1);

        when(distanceCalculator.calculateDistance(wayPoint0, wayPoint1)).thenReturn(Distance.ofMeters(1000.0));
        when(speedCalculator.calculateSpeed(Distance.ofMeters(1000.0), Duration.zero()))
            .thenReturn(Speed.zero());

        List<Speed> actualMetrics = calculator.calculateKilometerSpeeds(wayPoints);

        assertEquals(1, actualMetrics.size(), "Expected one speed entry");
        assertEquals(0.0, actualMetrics.getFirst().toMetersPerSecond(), "Expected zero speed");

        verify(speedCalculator, times(1))
            .calculateSpeed(Distance.ofMeters(1000.0), Duration.zero());
        verifyNoMoreInteractions(speedCalculator);
    }

    @Test
    void calculateKilometerSpeeds_withWayPointsNotSortedByTime_throwsWayPointsNotSortedException() {
        Instant t0 = Instant.parse("2025-08-22T10:00:00Z");
        Instant t1 = t0.minusSeconds(1);

        WayPoint wayPoint0 = WayPoint.builder().lat(0.0).lon(0.0).time(t0).build();
        WayPoint wayPoint1 = WayPoint.builder().lat(0.0).lon(0.001).time(t1).build();
        List<WayPoint> wayPoints = List.of(wayPoint0, wayPoint1);

        assertThrows(WayPointsNotSortedException.class, () -> calculator.calculateKilometerSpeeds(wayPoints));
    }
}
