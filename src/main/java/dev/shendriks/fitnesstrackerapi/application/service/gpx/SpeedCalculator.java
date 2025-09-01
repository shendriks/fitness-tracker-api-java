package dev.shendriks.fitnesstrackerapi.application.service.gpx;

import dev.shendriks.fitnesstrackerapi.domain.value.Distance;
import dev.shendriks.fitnesstrackerapi.domain.value.Duration;
import dev.shendriks.fitnesstrackerapi.domain.value.Speed;

public interface SpeedCalculator {
    Speed calculateSpeed(Distance distance, Duration duration);
}
