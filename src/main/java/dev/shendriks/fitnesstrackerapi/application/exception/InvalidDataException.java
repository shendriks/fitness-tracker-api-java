package dev.shendriks.fitnesstrackerapi.application.exception;

/**
 * Base exception indicating that a client provided invalid or inconsistent data.
 *
 * <p>Concrete subclasses provide more specific reasons such as email conflicts
 * or password mismatches.</p>
 */
public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String message) {
        super(message);
    }
}