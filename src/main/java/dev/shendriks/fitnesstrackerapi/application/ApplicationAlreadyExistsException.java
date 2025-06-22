package dev.shendriks.fitnesstrackerapi.application;

import dev.shendriks.fitnesstrackerapi.error.ApiException;
import org.springframework.http.HttpStatus;

public class ApplicationAlreadyExistsException extends ApiException {
    public ApplicationAlreadyExistsException() {
        super(HttpStatus.BAD_REQUEST, "An application with this name already exists");
    }
}
