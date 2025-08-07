package dev.shendriks.fitnesstrackerapi.domain.gpx.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = GPXFileConstraintValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface GPXFile {
    String message() default "Invalid GPX file: {error}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
