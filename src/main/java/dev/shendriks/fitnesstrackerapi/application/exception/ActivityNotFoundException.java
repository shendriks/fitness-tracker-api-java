package dev.shendriks.fitnesstrackerapi.application.exception;

public class ActivityNotFoundException extends ObjectNotFoundException {
    public ActivityNotFoundException() {
        super("Activity not found");
    }
}
