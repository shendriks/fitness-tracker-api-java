package dev.shendriks.fitnesstrackerapi.domain.activity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ActivityType {
    WALKING("walking"),
    RUNNING("running"),
    SWIMMING("swimming"),
    MOUNTAIN_BIKING("mountain_biking"),
    CYCLING("cycling");

    private final String value;

    ActivityType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ActivityType fromString(String text) {
        for (ActivityType type : ActivityType.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
