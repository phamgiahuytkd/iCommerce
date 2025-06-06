package com.example.iCommerce.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = {PasswordValidator.class}
)
public @interface PasswordConstraint {

    int min();
    boolean containUpperChar();
    boolean containSpecialChar();

    String message() default "Ngày sinh không đúng";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};



}
