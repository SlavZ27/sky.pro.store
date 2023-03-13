package ru.skypro.homework.validate;

import ru.skypro.homework.entity.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidPhoneValidatorUser implements ConstraintValidator<ValidPhoneUser, User> {

    @Override
    public void initialize(ValidPhoneUser constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext constraintValidatorContext) {
        if (user == null || user.getPhone() == null || user.getPhone().length() == 0) {
            return true;
        }
        return Validate.validatePhone(user.getPhone());
    }
}
