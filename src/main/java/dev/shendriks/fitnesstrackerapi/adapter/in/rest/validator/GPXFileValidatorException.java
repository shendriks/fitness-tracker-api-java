package dev.shendriks.fitnesstrackerapi.adapter.in.rest.validator;

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
        return new GPXFileValidatorException("The gpxFile must be a GPX gpxFile (.gpx)");
    }

    public static GPXFileValidatorException becauseFileContainsNoTrack() {
        return new GPXFileValidatorException("The gpxFile must contain at least one track");
    }

    public static GPXFileValidatorException becauseInvalidLatitudeLongitudeValues() {
        return new GPXFileValidatorException("The gpxFile contains invalid latitude or longitude values");
    }

    public static GPXFileValidatorException becauseFileContainsTooFewTrackPoints() {
        return new GPXFileValidatorException("The gpxFile must contain at least two track points");
    }

    public static GPXFileValidatorException becauseFileIsNoValidXMLFile() {
        return new GPXFileValidatorException("The gpxFile is no valid XML gpxFile");
    }

    public static GPXFileValidatorException becauseWrongVersion() {
        return new GPXFileValidatorException("The gpxFile must be a GPX 1.1 gpxFile");
    }

    public static GPXFileValidatorException becauseTimeElementIsMissing() {
        return new GPXFileValidatorException("The gpxFile contains at least one track point with no time element");
    }
}
