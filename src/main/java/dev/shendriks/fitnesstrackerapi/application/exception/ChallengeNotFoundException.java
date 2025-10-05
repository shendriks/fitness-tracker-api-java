package dev.shendriks.fitnesstrackerapi.application.exception;

/**
 * Thrown when a challenge with the given identifier cannot be found.
 */
public class ChallengeNotFoundException extends ObjectNotFoundException {
    public ChallengeNotFoundException(String id) {
        super("Challenge with id " + id + " not found");
    }
}
