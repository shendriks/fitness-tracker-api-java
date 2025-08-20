package dev.shendriks.fitnesstrackerapi.application.exception;


public class ChallengeNotFoundException extends ObjectNotFoundException {
    public ChallengeNotFoundException(String id) {
        super("Challenge with id " + id + " not found");
    }
}
