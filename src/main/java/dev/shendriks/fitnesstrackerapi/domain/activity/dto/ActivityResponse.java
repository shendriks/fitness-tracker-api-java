package dev.shendriks.fitnesstrackerapi.domain.activity.dto;

import com.fasterxml.jackson.annotation.JsonView;
import dev.shendriks.fitnesstrackerapi.domain.activity.enums.ActivityType;
import dev.shendriks.fitnesstrackerapi.domain.activity.view.View;

import java.time.Instant;
import java.util.List;

public record ActivityResponse(
    String id,
    ActivityType activityType,
    int duration,
    int calories,
    Instant createdAt,
    Instant updatedAt,
    String title,
    String description,
    int distance,
    Instant startDate,
//    @JsonView(View.WithGpsPositions.class)
    List<GPSPositionResponse> gpsPositions
) {
}
