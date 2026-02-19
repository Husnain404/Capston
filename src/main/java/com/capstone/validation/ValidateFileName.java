package com.capstone.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FileNameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateFileName {
    String message() default "Invalid file name. Must be TRIAL_BALANCE_YYYYMMDD with a valid date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
