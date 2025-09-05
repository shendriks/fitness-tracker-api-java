package dev.shendriks.fitnesstrackerapi.domain.value;

import java.util.Arrays;
import java.util.Base64;

public record ImageData(byte[] bytes) {
    public String toBase64String() {
        return Base64.getEncoder().encodeToString(bytes);
    }

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
