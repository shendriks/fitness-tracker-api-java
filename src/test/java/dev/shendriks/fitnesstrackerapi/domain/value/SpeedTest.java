package dev.shendriks.fitnesstrackerapi.domain.value;

import dev.shendriks.fitnesstrackerapi.infrastructure.Constant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SpeedTest {
    @Test
    void speed_worksAsExpected() {
        Speed speed = Speed.ofMetersPerSecond(10.0);
        assertEquals(10.0, speed.toMetersPerSecond(), Constant.EPSILON, "Expected speed of 10m/s");

        Pace pace = speed.toPace();
        assertInstanceOf(Pace.class, pace);
        assertEquals(100.0, pace.toSecondsPerKilometer(), Constant.EPSILON, "Expected pace of 100s/km");

        speed = Speed.ofMetersPerSecond(0.0);
        assertEquals(0.0, speed.toMetersPerSecond(), Constant.EPSILON, "Expected speed of 0m/s");

        pace = speed.toPace();
        assertInstanceOf(Pace.class, pace);
        assertEquals(0.0, pace.toSecondsPerKilometer(), Constant.EPSILON, "Expected pace of 0s/km");
    }

    @Test
    void speed_equals_worksAsExpected() {
        Speed speed1 = Speed.ofMetersPerSecond(10.0);
        Speed speed2 = Speed.ofMetersPerSecond(10.0);
        Speed speed3 = Speed.ofMetersPerSecond(20.0);
        assertEquals(speed1, speed2, "Expected speeds to be equal");
        assertNotEquals(speed1, speed3, "Expected speeds to be different");
    }
}
