package com.capstone.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

@Component
public class FileNameValidator implements ConstraintValidator<ValidateFileName, String> {
    private static final String PREFIX = "TRIAL_BALANCE_";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd")
            .withResolverStyle(ResolverStyle.STRICT);

    @Override
    public boolean isValid(String fileName, ConstraintValidatorContext context) {
        if (fileName == null || !fileName.startsWith(PREFIX)) {
            return false;
        }

        if (fileName.length() != PREFIX.length() + 8) {
            return false;
        }

        String datePart = fileName.substring(PREFIX.length());

        try {
            LocalDate.parse(datePart, DATE_FORMAT);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
