package dev.shendriks.fitnesstrackerapi.application.exception;

public class UserNotFoundException extends ObjectNotFoundException {
    public UserNotFoundException() {
        super("User not found");
    }
}
