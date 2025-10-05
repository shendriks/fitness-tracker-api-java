package dev.shendriks.fitnesstrackerapi.application.exception;

/**
 * Thrown when an activity with the requested identifier does not exist.
 */
public class ActivityNotFoundException extends ObjectNotFoundException {
    public ActivityNotFoundException() {
        super("Activity not found");
    }
}
