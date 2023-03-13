package ru.skypro.homework.validate;

import ru.skypro.homework.dto.RegisterReqDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidPhoneValidator implements ConstraintValidator<ValidPhone, String> {

    @Override
    public void initialize(ValidPhone constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext constraintValidatorContext) {
        if (phone == null) {
            return true;
        }
        if (!phone.startsWith("+79")) {
            return false;
        }
        phone = phone.substring(3);
        if (phone.length() != 9) {
            return false;
        }
        try {
            Integer.parseInt(phone);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
