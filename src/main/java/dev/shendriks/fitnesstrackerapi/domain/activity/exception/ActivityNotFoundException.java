package dev.shendriks.fitnesstrackerapi.domain.activity.exception;

import dev.shendriks.fitnesstrackerapi.error.ApiException;
import org.springframework.http.HttpStatus;

public class ActivityNotFoundException extends ApiException {
    public ActivityNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Activity not found");
    }
}
