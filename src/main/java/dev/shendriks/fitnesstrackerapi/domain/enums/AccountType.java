package dev.shendriks.fitnesstrackerapi.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AccountType {
    BASIC("basic"),
    PREMIUM("premium");

    private final String value;

    AccountType(String value) {
        this.value = value;
    }

    @JsonCreator
    public static AccountType fromString(String text) {
        for (AccountType accountType : AccountType.values()) {
            if (accountType.value.equalsIgnoreCase(text)) {
                return accountType;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}

