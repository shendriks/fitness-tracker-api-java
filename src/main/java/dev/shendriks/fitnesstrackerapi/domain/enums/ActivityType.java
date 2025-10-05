package dev.shendriks.fitnesstrackerapi.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration of supported activity types.
 *
 * <p>Provides Jackson-friendly string values for serialization/deserialization.</p>
 */
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

    /**
     * Parses an ActivityType from its string value (case-insensitive).
     * @param text string value of the activity type
     * @return matching ActivityType
     * @throws IllegalArgumentException if no type matches the text
     */
    @JsonCreator
    public static ActivityType fromString(String text) {
        for (ActivityType type : ActivityType.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }

    /**
     * Returns the canonical string value used for JSON serialization.
     */
    @JsonValue
    public String getValue() {
        return value;
    }
}
