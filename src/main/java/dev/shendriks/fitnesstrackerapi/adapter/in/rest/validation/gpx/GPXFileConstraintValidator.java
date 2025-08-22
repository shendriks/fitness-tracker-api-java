package dev.shendriks.fitnesstrackerapi.adapter.in.rest.validation.gpx;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.java.Log;
import org.springframework.web.multipart.MultipartFile;

@Log
public class GPXFileConstraintValidator implements ConstraintValidator<GPXFile, MultipartFile> {
    private final GPXFileValidator validator;

    public GPXFileConstraintValidator(GPXFileValidator validator) {
        this.validator = validator;
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        try {
            validator.validate(file);
        } catch (GPXFileValidatorException e) {
            context.disableDefaultConstraintViolation();
            context
                .buildConstraintViolationWithTemplate(e.getMessage())
                .addConstraintViolation();
            return false;
        }

        return true;
    }
}
