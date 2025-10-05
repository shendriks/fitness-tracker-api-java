package dev.shendriks.fitnesstrackerapi.application.exception;

/**
 * Thrown when a user with the requested identifier cannot be found.
 */
public class UserNotFoundException extends ObjectNotFoundException {
    public UserNotFoundException() {
        super("User not found");
    }
}
