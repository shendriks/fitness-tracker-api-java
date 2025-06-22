package dev.shendriks.fitnesstrackerapi.domain.developer.exception;

import dev.shendriks.fitnesstrackerapi.error.ApiException;
import org.springframework.http.HttpStatus;

public class DeveloperNotFoundException extends ApiException {
    public DeveloperNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Developer not found");
    }
}
