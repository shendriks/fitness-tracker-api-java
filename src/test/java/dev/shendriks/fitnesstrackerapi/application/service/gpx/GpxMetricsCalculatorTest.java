package dev.shendriks.fitnesstrackerapi.application.service.gpx;

import dev.shendriks.fitnesstrackerapi.application.service.gpx.distance.DistanceCalculator;
import dev.shendriks.fitnesstrackerapi.domain.value.Distance;
import dev.shendriks.fitnesstrackerapi.domain.value.Duration;
import dev.shendriks.fitnesstrackerapi.domain.value.MotionAndPausingTime;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GpxMetricsCalculatorTest {
    @Mock
    private DistanceCalculator distanceCalculator;
    @Mock
    private SpeedCalculator speedCalculator;
    private GpxMetricsCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new GpxMetricsCalculator(distanceCalculator, speedCalculator);
    }

    @Test
    void calculateDistance_withNullOrTooFewPoints_returnsZero() {
        assertEquals(0.0, calculator.calculateDistance(null).toMeters(), Constant.EPSILON, "Expected null to return length of 0m");
        assertEquals(0.0, calculator.calculateDistance(List.of()).toMeters(), Constant.EPSILON, "Expected empty list to return length of 0m");
        assertEquals(
            0.0,
            calculator.calculateDistance(List.of(
                WayPoint.builder().lat(0).lon(0).time(Instant.parse("2025-08-01T00:00:00Z")).build()
            )).toMeters(),
            Constant.EPSILON,
            "Expected list with only one way point to return length of 0m"
        );
    }

    @Test
    void calculateDistance_sumsConsecutiveDistances() {
        Instant t0 = Instant.parse("2025-08-22T10:00:00Z");
        Instant t1 = t0.plusSeconds(10);
        Instant t2 = t1.plusSeconds(10);

        List<WayPoint> points = List.of(
            WayPoint.builder().lat(0).lon(0).time(t0).build(),
            WayPoint.builder().lat(0.1).lon(0.2).time(t1).build(),
            WayPoint.builder().lat(0.2).lon(0.4).time(t2).build()
        );

        when(distanceCalculator.calculateDistance(points.get(0), points.get(1))).thenReturn(Distance.ofMeters(10.0));
        when(distanceCalculator.calculateDistance(points.get(1), points.get(2))).thenReturn(Distance.ofMeters(20.0));

        Distance actualTotalLength = calculator.calculateDistance(points);

        assertEquals(30.0, actualTotalLength.toMeters(), Constant.EPSILON, "Expected total length to be 30m");
    }

    @Test
    void calculateDuration_withTimesPresent_returnsDurationOtherwiseZero() {
        Instant t0 = Instant.parse("2025-08-22T10:00:00Z");
        Instant t1 = t0.plusSeconds(300);
        Instant t2 = t1.plusSeconds(330);

        List<WayPoint> points = List.of(
            WayPoint.builder().lat(0).lon(0).time(t0).build(),
            WayPoint.builder().lat(0.1).lon(0.2).time(t1).build(),
            WayPoint.builder().lat(0.2).lon(0.4).time(t2).build()
        );
        assertEquals(630.0, calculator.calculateDuration(points).toSeconds(), Constant.EPSILON, "Expected duration to be 630s (10m 30s)");

        List<WayPoint> missingFirst = List.of(
            WayPoint.builder().lat(0).lon(0).build(),
            WayPoint.builder().lat(0.1).lon(0.2).time(t1).build(),
            WayPoint.builder().lat(0.2).lon(0.4).time(t2).build()
        );
        assertEquals(0.0, calculator.calculateDuration(missingFirst).toSeconds(), Constant.EPSILON, "Expected duration to be 0s (missing first time)");

        List<WayPoint> missingLast = List.of(
            WayPoint.builder().lat(0).lon(0).time(t0).build(),
            WayPoint.builder().lat(0.1).lon(0.2).time(t1).build(),
            WayPoint.builder().lat(0.2).lon(0.4).build()
        );
        assertEquals(0.0, calculator.calculateDuration(missingLast).toSeconds(), Constant.EPSILON, "Expected duration to be 0s (missing last time)");
    }

    @Test
    void calculateElevationGain_sumsOnlyPositiveElevationChangesAndIgnoresMissing() {
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

        Distance actualElevationGain = calculator.calculateElevationGain(points);

        assertEquals(80.0, actualElevationGain.toMeters(), Constant.EPSILON, "Expected elevation gain of 80m");
    }

    @Test
    void calculateElevationGain_whenNull_returnsZero() {
        Distance actualElevationGain = calculator.calculateElevationGain(null);

        assertEquals(0.0, actualElevationGain.toMeters(), Constant.EPSILON, "Expected elevation gain of 0m");
    }

    @Test
    void calculateElevationGain_whenOnlyOnePoint_returnsZero() {
        Instant t0 = Instant.parse("2025-08-22T10:00:00Z");
        List<WayPoint> points = List.of(WayPoint.builder().lat(0).lon(0).time(t0).ele(100.0).build());

        Distance actualElevationGain = calculator.calculateElevationGain(points);

        assertEquals(0.0, actualElevationGain.toMeters(), Constant.EPSILON, "Expected elevation gain of 0m");
    }

    @Test
    void calculateMotionAndPausingTime_classifiesBySpeedThresholdAndSkipsMissingTimes() {
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
        WayPoint wayPoint6 = WayPoint.builder().lat(0.5).lon(1.0).time(t4).build(); // same time as wayPoint5

        List<WayPoint> points = List.of(wayPoint1, wayPoint2, wayPoint3, wayPoint4, wayPoint5, wayPoint6);

        // Segment wayPoint1 -> wayPoint2: 5m over 10s => 0.5 m/s (threshold) -> motion
        // Segment wayPoint2 -> wayPoint3: 2m over 10s => 0.2 m/s (under threshold) -> pause
        // Segment wayPoint3 -> wayPoint5: 20m over 20s => 1.0m/s (over threshold) -> motion
        // Segment wayPoint5 -> wayPoint6: 10m over 0s => skipped
        when(distanceCalculator.calculateDistance(wayPoint1, wayPoint2)).thenReturn(Distance.ofMeters(5.0));
        when(distanceCalculator.calculateDistance(wayPoint2, wayPoint3)).thenReturn(Distance.ofMeters(2.0));
        when(distanceCalculator.calculateDistance(wayPoint3, wayPoint5)).thenReturn(Distance.ofMeters(20.0));
        when(distanceCalculator.calculateDistance(wayPoint5, wayPoint6)).thenReturn(Distance.ofMeters(10.0));
        when(speedCalculator.calculateSpeed(Distance.ofMeters(5.0), Duration.ofSeconds(10L)))
            .thenReturn(Speed.ofMetersPerSecond(0.5));
        when(speedCalculator.calculateSpeed(Distance.ofMeters(2.0), Duration.ofSeconds(10L)))
            .thenReturn(Speed.ofMetersPerSecond(0.2));
        when(speedCalculator.calculateSpeed(Distance.ofMeters(20.0), Duration.ofSeconds(20L)))
            .thenReturn(Speed.ofMetersPerSecond(1.0));
        when(speedCalculator.calculateSpeed(Distance.ofMeters(10.0), Duration.zero()))
            .thenReturn(Speed.zero());

        MotionAndPausingTime actualMotionAndPausingTime = calculator.calculateMotionAndPausingTime(points);

        assertEquals(
            MotionAndPausingTime
                .builder()
                .motionTime(Duration.ofSeconds(30L))
                .pausingTime(Duration.ofSeconds(10L))
                .build(),
            actualMotionAndPausingTime,
            "Expected motion time of 30s and pausing time of 10s"
        );

        verify(speedCalculator, times(1))
            .calculateSpeed(Distance.ofMeters(5.0), Duration.ofSeconds(10L));
        verify(speedCalculator, times(1))
            .calculateSpeed(Distance.ofMeters(2.0), Duration.ofSeconds(10L));
        verify(speedCalculator, times(1))
            .calculateSpeed(Distance.ofMeters(5.0), Duration.ofSeconds(10L));
        verify(speedCalculator, times(1))
            .calculateSpeed(Distance.ofMeters(10.0), Duration.zero());
        verifyNoMoreInteractions(speedCalculator);
    }
}

