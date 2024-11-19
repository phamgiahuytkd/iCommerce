package com.example.iCommerce.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class NotNullValidator implements ConstraintValidator<NotNullConstraint, String> {

    private boolean notNull;
    private boolean notEmptyString;
    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {

        if(notNull && string == null)
            return false;

        if(notEmptyString && Objects.equals(string, ""))
            return false;


        return true;
    }

    @Override
    public void initialize(NotNullConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        notNull = constraintAnnotation.notNull();
        notEmptyString = constraintAnnotation.notEmptyString();

    }
}
