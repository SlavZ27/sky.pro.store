package ru.skypro.homework.validate;

import ru.skypro.homework.entity.Role;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * Provides implementations of {@link ConstraintValidator} methods.
 */
public class ValidRoleValidator implements ConstraintValidator<ValidRole, String> {

    @Override
    public void initialize(ValidRole constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * Checks the validity of the role
     *
     * @param role                       the role
     * @param constraintValidatorContext the constraint validator context
     * @return the boolean
     */
    @Override
    public boolean isValid(String role, ConstraintValidatorContext constraintValidatorContext) {
        if (role == null) {
            return true;
        }
        try {
            Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
