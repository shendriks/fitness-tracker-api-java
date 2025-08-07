package dev.shendriks.fitnesstrackerapi.domain.gpx.validator;

public class GPXFileValidatorException extends RuntimeException {
    public GPXFileValidatorException(String message) {
        super(message);
    }

    public static GPXFileValidatorException becauseFileIsEmpty() {
        return new GPXFileValidatorException("File is empty");
    }

    public static GPXFileValidatorException becauseFileIsTooBig() {
        return new GPXFileValidatorException("File is too big");
    }

    public static GPXFileValidatorException becauseWrongSuffix() {
        return new GPXFileValidatorException("The file must be a GPX file (.gpx)");
    }

    public static GPXFileValidatorException becauseFileContainsNoTrack() {
        return new GPXFileValidatorException("The file must contain at least one track");
    }

    public static GPXFileValidatorException becauseInvalidLatitudeLongitudeValues() {
        return new GPXFileValidatorException("The file contains invalid latitude or longitude values");
    }

    public static GPXFileValidatorException becauseFileContainsTooFewTrackPoints() {
        return new GPXFileValidatorException("The file must contain at least two track points");
    }

    public static GPXFileValidatorException becauseFileIsNoValidXMLFile() {
        return new GPXFileValidatorException("The file is no valid XML file");
    }

    public static GPXFileValidatorException becauseWrongVersion() {
        return new GPXFileValidatorException("The file must be a GPX 1.1 file");
    }

    public static GPXFileValidatorException becauseTimeElementIsMissing() {
        return new GPXFileValidatorException("The file contains at least one track point with no time element");
    }
}
