package dev.shendriks.fitnesstrackerapi.infrastructure.supportive.ulid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UlidGeneratorTest {
    private static final String ULID_REGEX = "^[A-Z0-9]{26}$";

    @Test
    void generate_generatesValidUlid() {
        UlidGenerator generator = new UlidGenerator();

        Object actualResult = generator.generate(null, null, null, null);

        assertInstanceOf(String.class, actualResult);
        assertTrue(((String) actualResult).matches(ULID_REGEX), "Expected " + actualResult + " to match " + ULID_REGEX);
    }

    @Test
    void generate_generatesSuccessiveUlids() {
        UlidGenerator generator = new UlidGenerator();

        Object actualResult1 = generator.generate(null, null, null, null);
        Object actualResult2 = generator.generate(null, null, null, null);

        assertInstanceOf(String.class, actualResult1);
        assertInstanceOf(String.class, actualResult2);
        assertTrue(((String) actualResult1).compareTo((String) actualResult2) < 0, "Expected 1st ULID < 2nd ULID");
    }
}