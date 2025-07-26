package dev.shendriks.fitnesstrackerapi.domain.user.exception;

import dev.shendriks.fitnesstrackerapi.domain.exception.InvalidDataException;

public class EmailAlreadyRegisteredException extends InvalidDataException {
    public EmailAlreadyRegisteredException() {
        super("Email already registered");
    }
}
