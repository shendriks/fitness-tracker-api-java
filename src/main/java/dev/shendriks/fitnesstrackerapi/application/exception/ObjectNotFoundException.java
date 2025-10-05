package dev.shendriks.fitnesstrackerapi.application.exception;

/**
 * Base exception for resources that cannot be found.
 *
 * <p>Specialized subclasses indicate which type of object was missing.</p>
 */
public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String message) {
        super(message);
    }
}