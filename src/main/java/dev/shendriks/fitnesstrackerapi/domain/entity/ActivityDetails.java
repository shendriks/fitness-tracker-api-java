package dev.shendriks.fitnesstrackerapi.domain.entity;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.value.*;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

/**
 * Domain aggregate representing a recorded activity including full GPS details.
 *
 * <p>Extends Activity by including GPS positions, kilometer speeds, elevation gain, and motion/pausing time.</p>
 */
@Builder
public record ActivityDetails(
    ActivityId id,
    ActivityUlid ulid,
    ActivityType activityType,
    Duration duration,
    Distance distance,
    Speed averageSpeed,
    Instant createdAt,
    Instant updatedAt,
    String title,
    String description,
    Instant startDate,
    List<GPSPosition> gpsPositions,
    List<Speed> kilometerSpeeds,
    Distance elevationGain,
    Duration motionTime,
    Duration pausingTime
) {
}
