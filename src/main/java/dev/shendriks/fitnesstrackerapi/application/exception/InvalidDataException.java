package dev.shendriks.fitnesstrackerapi.application.exception;

public class InvalidDataException extends RuntimeException {
    public InvalidDataException(String message) {
        super(message);
    }
}