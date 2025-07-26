package dev.shendriks.fitnesstrackerapi.domain.activity.exception;

import dev.shendriks.fitnesstrackerapi.domain.exception.ObjectNotFoundException;

public class ActivityNotFoundException extends ObjectNotFoundException {
    public ActivityNotFoundException() {
        super("Activity not found");
    }
}
