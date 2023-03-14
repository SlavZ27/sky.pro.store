package ru.skypro.homework.validate;

import ru.skypro.homework.entity.Role;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidRoleValidator implements ConstraintValidator<ValidRole, String> {

    @Override
    public void initialize(ValidRole constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String role, ConstraintValidatorContext constraintValidatorContext) {
        if (role == null) {
            return false;
        }
        try {
            Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
