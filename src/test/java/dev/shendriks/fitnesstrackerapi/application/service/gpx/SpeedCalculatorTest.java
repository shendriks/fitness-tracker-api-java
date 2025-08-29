package dev.shendriks.fitnesstrackerapi.application.service.gpx;

import dev.shendriks.fitnesstrackerapi.domain.value.Distance;
import dev.shendriks.fitnesstrackerapi.domain.value.Duration;
import dev.shendriks.fitnesstrackerapi.infrastructure.Constant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpeedCalculatorTest {
    private SpeedCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new SpeedCalculatorImpl();
    }

    @Test
    void calculateSpeedAndPace_workAsExpected() {
        Double speed = calculator.calculateSpeed(Distance.ofMeters(100.0), Duration.zero()).toMetersPerSecond();
        assertEquals(0.0, speed, Constant.EPSILON, "Expected speed of 0m/s for 0 duration");

        speed = calculator.calculateSpeed(Distance.ofMeters(100.0), Duration.ofSeconds(10.0)).toMetersPerSecond();
        assertEquals(10.0, speed, "Expected speed of 10m/s for 100m in 10s");
    }
}
