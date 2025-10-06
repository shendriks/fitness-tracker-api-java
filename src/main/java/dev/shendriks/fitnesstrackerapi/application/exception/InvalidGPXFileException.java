package dev.shendriks.fitnesstrackerapi.application.exception;

public class InvalidGPXFileException extends InvalidDataException {
    public InvalidGPXFileException() {
        super("Unable to parse GPX file");
    }
}
