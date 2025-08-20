package dev.shendriks.fitnesstrackerapi.application.exception;

public class EmailAlreadyRegisteredException extends InvalidDataException {
    public EmailAlreadyRegisteredException() {
        super("Email already registered");
    }
}
