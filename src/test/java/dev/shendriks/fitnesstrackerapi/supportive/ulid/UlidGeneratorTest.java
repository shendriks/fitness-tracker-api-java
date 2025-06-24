package dev.shendriks.fitnesstrackerapi.supportive.ulid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UlidGeneratorTest {

    private static final String ULID_REGEX = "^[A-Z0-9]{26}$";

    @Test
    void generate() {
        UlidGenerator generator = new UlidGenerator();

        Object actualResult = generator.generate(null, null, null, null);

        assertInstanceOf(String.class, actualResult);
        assertTrue(((String) actualResult).matches(ULID_REGEX), "Expected " + actualResult + " to match " + ULID_REGEX);
    }
}