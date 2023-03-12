package ru.skypro.homework.validate;

import ru.skypro.homework.dto.RegisterReqDto;
import ru.skypro.homework.dto.UserDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidPhoneValidatorUserDto implements ConstraintValidator<ValidPhone, UserDto> {
    @Override
    public void initialize(ValidPhone constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserDto userDto, ConstraintValidatorContext constraintValidatorContext) {
        if (userDto == null || userDto.getPhone() == null || userDto.getPhone().length() == 0) {
            return true;
        }
        return Validate.validatePhone(userDto.getPhone());
    }
}
