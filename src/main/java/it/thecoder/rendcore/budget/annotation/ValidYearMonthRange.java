package it.thecoder.rendcore.budget.annotation;

import it.thecoder.rendcore.budget.validator.YearMonthRangeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = YearMonthRangeValidator.class)
@Documented
public @interface ValidYearMonthRange {
    String message() default
            "The end date must be greater than or equal to start date.";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
