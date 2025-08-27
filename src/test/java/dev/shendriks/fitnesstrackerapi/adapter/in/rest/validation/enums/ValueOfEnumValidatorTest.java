package dev.shendriks.fitnesstrackerapi.adapter.in.rest.validation.enums;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValueOfEnumValidatorTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void uppercaseValue_isInvalid() {
        TestRecord record = new TestRecord("VALUE_1");
        Set<ConstraintViolation<TestRecord>> violations = validator.validate(record);
        assertEquals(1, violations.size(), "Expected 1 violation");
        ConstraintViolation<TestRecord> violation = violations.iterator().next();
        assertEquals("value", violation.getPropertyPath().toString());
        assertEquals("must be any of [value_1, value_2]", violation.getMessage());
    }

    @Test
    void invalidValue_isInvalid() {
        TestRecord record = new TestRecord("value_3");
        Set<ConstraintViolation<TestRecord>> violations = validator.validate(record);
        assertEquals(1, violations.size(), "Expected 1 violation");
        ConstraintViolation<TestRecord> violation = violations.iterator().next();
        assertEquals("value", violation.getPropertyPath().toString());
        assertEquals("must be any of [value_1, value_2]", violation.getMessage());
    }

    @Test
    void validValue_isValid() {
        TestRecord record = new TestRecord("value_1");
        Set<ConstraintViolation<TestRecord>> violations = validator.validate(record);
        assertEquals(0, violations.size(), "Expected no violations");
    }

    private enum TestEnum {
        VALUE_1,
        VALUE_2
    }

    private record TestRecord(
        @ValueOfEnum(enumClass = TestEnum.class)
        String value
    ) {
    }
}
