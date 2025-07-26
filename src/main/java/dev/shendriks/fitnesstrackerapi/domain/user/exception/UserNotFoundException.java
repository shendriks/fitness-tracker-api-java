package dev.shendriks.fitnesstrackerapi.domain.user.exception;

import dev.shendriks.fitnesstrackerapi.domain.exception.ObjectNotFoundException;

public class UserNotFoundException extends ObjectNotFoundException {
    public UserNotFoundException() {
        super("User not found");
    }
}
