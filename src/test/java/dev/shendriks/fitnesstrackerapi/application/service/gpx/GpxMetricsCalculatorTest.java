package dev.shendriks.fitnesstrackerapi.application.service.gpx;

import dev.shendriks.fitnesstrackerapi.application.service.gpx.distance.DistanceCalculator;
import dev.shendriks.fitnesstrackerapi.domain.value.MotionAndPausingTime;
import io.jenetics.jpx.WayPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GpxMetricsCalculatorTest {
    @Mock
    private DistanceCalculator distanceCalculator;
    private GpxMetricsCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new GpxMetricsCalculator(distanceCalculator);
    }

    @Test
    void calculateTotalLength_nullOrTooFewPoints_returnsZero() {
        assertEquals(0.0, calculator.calculateTotalLength(null), "Expected null to return length of 0m");
        assertEquals(0.0, calculator.calculateTotalLength(List.of()), "Expected empty list to return length of 0m");
        assertEquals(
            0.0,
            calculator.calculateTotalLength(List.of(
                WayPoint.builder().lat(0).lon(0).time(Instant.parse("2025-08-01T00:00:00Z")).build()
            )),
            "Expected list with only one way point to return length of 0m"
        );
    }

    @Test
    void calculateTotalLength_sumsConsecutiveDistances() {
        Instant t0 = Instant.parse("2025-08-22T10:00:00Z");
        Instant t1 = t0.plusSeconds(10);
        Instant t2 = t1.plusSeconds(10);

        List<WayPoint> points = List.of(
            WayPoint.builder().lat(0).lon(0).time(t0).build(),
            WayPoint.builder().lat(0.1).lon(0.2).time(t1).build(),
            WayPoint.builder().lat(0.2).lon(0.4).time(t2).build()
        );

        when(distanceCalculator.calculateDistance(points.get(0), points.get(1))).thenReturn(10.0);
        when(distanceCalculator.calculateDistance(points.get(1), points.get(2))).thenReturn(20.0);

        double actualTotalLength = calculator.calculateTotalLength(points);

        assertEquals(30.0, actualTotalLength, "Expected total length to be 30m");
    }

    @Test
    void calculateDuration_betweenFirstAndLast_whenTimesPresent_otherwiseZero() {
        Instant t0 = Instant.parse("2025-08-22T10:00:00Z");
        Instant t1 = t0.plusSeconds(300);
        Instant t2 = t1.plusSeconds(330);

        List<WayPoint> points = List.of(
            WayPoint.builder().lat(0).lon(0).time(t0).build(),
            WayPoint.builder().lat(0.1).lon(0.2).time(t1).build(),
            WayPoint.builder().lat(0.2).lon(0.4).time(t2).build()
        );
        assertEquals(630L, calculator.calculateDuration(points), "Expected duration to be 630s (10m 30s)");

        List<WayPoint> missingFirst = List.of(
            WayPoint.builder().lat(0).lon(0).build(),
            WayPoint.builder().lat(0.1).lon(0.2).time(t1).build(),
            WayPoint.builder().lat(0.2).lon(0.4).time(t2).build()
        );
        assertEquals(0L, calculator.calculateDuration(missingFirst), "Expected duration to be 0s (missing first time)");

        List<WayPoint> missingLast = List.of(
            WayPoint.builder().lat(0).lon(0).time(t0).build(),
            WayPoint.builder().lat(0.1).lon(0.2).time(t1).build(),
            WayPoint.builder().lat(0.2).lon(0.4).build()
        );
        assertEquals(0L, calculator.calculateDuration(missingLast), "Expected duration to be 0s (missing last time)");
    }

    @Test
    void calculateSpeed_and_Pace_handleZeros_andNormalValues() {
        assertEquals(0.0, calculator.calculateSpeed(100.0, 0), "Expected speed of 0m/s for 0 duration");
        assertEquals(0.0, calculator.calculatePace(0), "Expected pace of 0s/km for 0 speed");

        double actualSpeed = calculator.calculateSpeed(100.0, 10);
        assertEquals(10.0, actualSpeed, "Expected speed of 10m/s for 100m in 10s");

        double actualPace = calculator.calculatePace(actualSpeed);
        assertEquals(100.0, actualPace, "Expected pace of 100s/km for 10m/s");
    }

    @Test
    void calculateElevationGain_sumsOnlyPositiveElevationChanges_andIgnoresMissing() {
        Instant t0 = Instant.parse("2025-08-22T10:00:00Z");
        Instant t1 = t0.plusSeconds(300);
        Instant t2 = t1.plusSeconds(300);
        Instant t3 = t2.plusSeconds(300);
        Instant t4 = t3.plusSeconds(300);
        Instant t5 = t4.plusSeconds(300);

        List<WayPoint> points = List.of(
            WayPoint.builder().lat(0).lon(0).time(t0).ele(100.0).build(),
            WayPoint.builder().lat(0.1).lon(0.2).time(t1).ele(150.0).build(),
            WayPoint.builder().lat(0.2).lon(0.4).time(t2).ele(140.0).build(),
            WayPoint.builder().lat(0.3).lon(0.6).time(t3).build(),
            WayPoint.builder().lat(0.4).lon(0.8).time(t4).ele(160.0).build(),
            WayPoint.builder().lat(0.5).lon(1.0).time(t5).ele(170.0).build()
        );

        double actualElevationGain = calculator.calculateElevationGain(points);

        assertEquals(80.0, actualElevationGain, "Expected elevation gain of 80m");
    }

    @Test
    void calculateMotionAndPausingTime_classifiesBySpeedThreshold_andSkipsMissingTimes() {
        Instant t0 = Instant.parse("2025-08-22T10:00:00Z");
        Instant t1 = t0.plusSeconds(10);
        Instant t2 = t1.plusSeconds(10);
        Instant t3 = t2.plusSeconds(10);
        Instant t4 = t3.plusSeconds(10);

        WayPoint wayPoint1 = WayPoint.builder().lat(0.0).lon(0.0).time(t0).build();
        WayPoint wayPoint2 = WayPoint.builder().lat(0.1).lon(0.2).time(t1).build();
        WayPoint wayPoint3 = WayPoint.builder().lat(0.2).lon(0.4).time(t2).build();
        WayPoint wayPoint4 = WayPoint.builder().lat(0.3).lon(0.6).build(); // missing time
        WayPoint wayPoint5 = WayPoint.builder().lat(0.4).lon(0.8).time(t4).build();

        List<WayPoint> points = List.of(wayPoint1, wayPoint2, wayPoint3, wayPoint4, wayPoint5);

        // Segment wayPoint1 → wayPoint2: 5m over 10s ⇒ 0.5 m/s (threshold) → motion
        // Segment wayPoint2 → wayPoint3: 2m over 10s ⇒ 0.2 m/s → pause
        // Segment wayPoint3 → wayPoint4: skipped (missing time)
        // Segment wayPoint4 → wayPoint5: skipped (missing time)
        when(distanceCalculator.calculateDistance(wayPoint1, wayPoint2)).thenReturn(5.0);
        when(distanceCalculator.calculateDistance(wayPoint2, wayPoint3)).thenReturn(2.0);

        MotionAndPausingTime actualMotionAndPausingTime = calculator.calculateMotionAndPausingTime(points);

        assertEquals(
            MotionAndPausingTime.builder().motionTime(10L).pausingTime(10L).build(),
            actualMotionAndPausingTime,
            "Expected motion and pausing time of 10s each"
        );
    }
}
