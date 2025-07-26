package dev.shendriks.fitnesstrackerapi.domain.user.exception;

import dev.shendriks.fitnesstrackerapi.domain.exception.InvalidDataException;

public class CurrentPasswordDoesntMatchException extends InvalidDataException {
    public CurrentPasswordDoesntMatchException() {
        super("Current password doesn't match");
    }
}
