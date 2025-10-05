package dev.shendriks.fitnesstrackerapi.domain.exception;

/**
 * Thrown when a sequence of waypoints is expected to be sorted by timestamp but is not.
 *
 * <p>Used by GPX processing and metrics calculations to enforce chronological order.</p>
 */
public class WayPointsNotSortedException extends RuntimeException {
}
