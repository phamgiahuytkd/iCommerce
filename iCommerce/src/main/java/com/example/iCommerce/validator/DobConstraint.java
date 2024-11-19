package com.example.iCommerce.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Size;

import java.lang.annotation.*;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = {DobValidator.class}
)
public @interface DobConstraint {

    int min();

    String message() default "Ngày sinh không đúng";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};



}
