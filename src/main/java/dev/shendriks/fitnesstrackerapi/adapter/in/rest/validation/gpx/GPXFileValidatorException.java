package dev.shendriks.fitnesstrackerapi.adapter.in.rest.validation.gpx;

/**
 * Exception thrown when a provided GPX file fails validation checks.
 *
 * <p>Use the static factory methods to create descriptive error instances for
 * common validation failures.</p>
 */
public class GPXFileValidatorException extends RuntimeException {
    /**
     * Creates a new GPXFileValidatorException with the given message.
     * @param message human-readable reason for the validation failure
     */
    public GPXFileValidatorException(String message) {
        super(message);
    }

    /** File was empty. */
    public static GPXFileValidatorException becauseFileIsEmpty() {
        return new GPXFileValidatorException("File is empty");
    }

    /** File exceeded the configured maximum size. */
    public static GPXFileValidatorException becauseFileIsTooBig() {
        return new GPXFileValidatorException("File is too big");
    }

    /** File extension was not .gpx. */
    public static GPXFileValidatorException becauseWrongSuffix() {
        return new GPXFileValidatorException("The gpxFile must be a GPX gpxFile (.gpx)");
    }

    /** No GPX track element was found in the file. */
    public static GPXFileValidatorException becauseFileContainsNoTrack() {
        return new GPXFileValidatorException("The gpxFile must contain at least one track");
    }

    /** Detected latitude/longitude values outside valid ranges. */
    public static GPXFileValidatorException becauseInvalidLatitudeLongitudeValues() {
        return new GPXFileValidatorException("The gpxFile contains invalid latitude or longitude values");
    }

    /** Not enough track points to compute a track. */
    public static GPXFileValidatorException becauseFileContainsTooFewTrackPoints() {
        return new GPXFileValidatorException("The gpxFile must contain at least two track points");
    }

    /** XML was malformed or not parseable as GPX. */
    public static GPXFileValidatorException becauseFileIsNoValidXMLFile() {
        return new GPXFileValidatorException("The gpxFile is no valid XML gpxFile");
    }

    /** GPX version was not 1.1. */
    public static GPXFileValidatorException becauseWrongVersion() {
        return new GPXFileValidatorException("The gpxFile must be a GPX 1.1 gpxFile");
    }

    /** A track point without a time element was encountered. */
    public static GPXFileValidatorException becauseTimeElementIsMissing() {
        return new GPXFileValidatorException("The gpxFile contains at least one track point with no time element");
    }
}
