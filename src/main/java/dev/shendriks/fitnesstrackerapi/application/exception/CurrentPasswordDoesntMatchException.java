package dev.shendriks.fitnesstrackerapi.application.exception;

public class CurrentPasswordDoesntMatchException extends InvalidDataException {
    public CurrentPasswordDoesntMatchException() {
        super("Current password doesn't match");
    }
}
