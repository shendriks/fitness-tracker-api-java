package dev.shendriks.fitnesstrackerapi.domain.challenge.exception;


import dev.shendriks.fitnesstrackerapi.domain.exception.ObjectNotFoundException;

public class ChallengeNotFoundException extends ObjectNotFoundException {
    public ChallengeNotFoundException(String id) {
        super("Challenge with id " + id + " not found");
    }
}
