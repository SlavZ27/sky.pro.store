package ru.skypro.homework.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * Provides implementations of {@link ConstraintValidator} methods
 */
public class ValidPhoneValidator implements ConstraintValidator<ValidPhone, String> {

    @Override
    public void initialize(ValidPhone constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * Checks the validity of the phone
     *
     * @param phone                      the phone
     * @param constraintValidatorContext the constraint validator context
     * @return the boolean
     */
    @Override
    public boolean isValid(String phone, ConstraintValidatorContext constraintValidatorContext) {
        if (phone == null) {
            return true;
        }
        if (!phone.startsWith("+79") || phone.length() != 12) {
            return false;
        }
        try {
            Integer.parseInt(phone.substring(3));
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
