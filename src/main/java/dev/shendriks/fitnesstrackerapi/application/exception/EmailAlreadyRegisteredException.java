package dev.shendriks.fitnesstrackerapi.application.exception;

/**
 * Indicates an attempt to register or update to an email address that is already in use.
 */
public class EmailAlreadyRegisteredException extends InvalidDataException {
    public EmailAlreadyRegisteredException() {
        super("Email already registered");
    }
}
