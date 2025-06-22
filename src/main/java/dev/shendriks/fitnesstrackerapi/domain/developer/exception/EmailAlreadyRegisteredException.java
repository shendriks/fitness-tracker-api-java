package dev.shendriks.fitnesstrackerapi.domain.developer.exception;

import dev.shendriks.fitnesstrackerapi.error.ApiException;
import org.springframework.http.HttpStatus;

public class EmailAlreadyRegisteredException extends ApiException {
    public EmailAlreadyRegisteredException() {
        super(HttpStatus.BAD_REQUEST, "Email already registered");
    }
}
