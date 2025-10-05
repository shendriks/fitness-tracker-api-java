package dev.shendriks.fitnesstrackerapi.domain.value;

import java.util.Arrays;
import java.util.Base64;

/**
 * Immutable wrapper for raw image bytes with utility helpers.
 */
public record ImageData(byte[] bytes) {
    /**
     * Encodes the image bytes as a Base64 string.
     * @return Base64-encoded string
     */
    public String toBase64String() {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Two ImageData instances are equal if their underlying byte arrays are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImageData other = (ImageData) o;
        return Arrays.equals(bytes, other.bytes());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes());
    }
}
