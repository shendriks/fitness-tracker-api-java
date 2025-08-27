package dev.shendriks.fitnesstrackerapi.domain.value;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;
import lombok.Builder;

@Builder
public record ActivityUpdateData(
    ActivityType activityType,
    String title,
    String description
) {
}
