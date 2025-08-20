package dev.shendriks.fitnesstrackerapi.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ActivityState {
    STARTED("started"),
    FINISHED("finished");

    private final String value;

    ActivityState(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ActivityState fromString(String text) {
        for (ActivityState type : ActivityState.values()) {
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
