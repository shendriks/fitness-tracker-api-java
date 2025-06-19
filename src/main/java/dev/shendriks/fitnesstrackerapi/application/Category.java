package dev.shendriks.fitnesstrackerapi.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Category {
    BASIC("basic"),
    PREMIUM("premium");

    private final String value;

    Category(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Category fromString(String text) {
        for (Category category : Category.values()) {
            if (category.value.equalsIgnoreCase(text)) {
                return category;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}

