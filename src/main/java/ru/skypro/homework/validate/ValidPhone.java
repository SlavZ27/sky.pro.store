package ru.skypro.homework.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPhoneValidator.class)
public @interface ValidPhone {
    String message() default "Phone number is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
