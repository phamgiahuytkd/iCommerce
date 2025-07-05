package com.example.iCommerce.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class PasswordValidator implements ConstraintValidator<PasswordConstraint, String> {

    private int min;
    private boolean containUpperChar;
    private boolean containSpecialChar;
    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        // Kiểm tra độ dài tối thiểu
        if (password.length() < min) {
            return false;
        }

        // Kiểm tra có chứa ký tự viết hoa (nếu yêu cầu)
        if (containUpperChar && !password.matches(".*[A-Z].*")) {
            return false;
        }

        // Kiểm tra có chứa ký tự đặc biệt (nếu yêu cầu)
        if (containSpecialChar && !password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            return false;
        }

        return true;
    }

    @Override
    public void initialize(PasswordConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.min();
        containUpperChar = constraintAnnotation.containUpperChar();
        containSpecialChar = constraintAnnotation.containSpecialChar();

    }
}
