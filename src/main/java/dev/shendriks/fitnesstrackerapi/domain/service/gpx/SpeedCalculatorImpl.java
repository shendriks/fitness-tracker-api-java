package dev.shendriks.fitnesstrackerapi.domain.service.gpx;

import dev.shendriks.fitnesstrackerapi.domain.value.Distance;
import dev.shendriks.fitnesstrackerapi.domain.value.Duration;
import dev.shendriks.fitnesstrackerapi.domain.value.Speed;
import org.springframework.stereotype.Component;

@Component
public class SpeedCalculatorImpl implements SpeedCalculator {
    @Override
    public Speed calculateSpeed(Distance distance, Duration duration) {
        return duration.toSeconds() > 0
            ? Speed.ofMetersPerSecond(distance.toMeters() / duration.toSeconds())
            : Speed.zero();
    }
}
