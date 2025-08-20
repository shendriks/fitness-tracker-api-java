package dev.shendriks.fitnesstrackerapi.domain.value;

import dev.shendriks.fitnesstrackerapi.domain.enums.ActivityType;

public record ActivityUpdateData(
    ActivityType activityType,
    String title,
    String description
) {
}
