package dev.shendriks.fitnesstrackerapi.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AchievementType {
    MILESTONE("milestone"),
    CHALLENGE("challenge");

    private final String value;

    AchievementType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
