package dev.shendriks.fitnesstrackerapi.application.exception;

/**
 * Raised when the provided current password does not match the user's stored password.
 */
public class CurrentPasswordDoesntMatchException extends InvalidDataException {
    public CurrentPasswordDoesntMatchException() {
        super("Current password doesn't match");
    }
}
