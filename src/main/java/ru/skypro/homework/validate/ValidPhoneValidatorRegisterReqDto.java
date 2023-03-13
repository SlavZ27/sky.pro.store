package ru.skypro.homework.validate;

import ru.skypro.homework.dto.RegisterReqDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidPhoneValidatorRegisterReqDto implements ConstraintValidator<ValidPhoneRegisterReqDto, RegisterReqDto> {

    @Override
    public void initialize(ValidPhoneRegisterReqDto constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(RegisterReqDto registerReqDto, ConstraintValidatorContext constraintValidatorContext) {
        if (registerReqDto == null || registerReqDto.getPhone() == null || registerReqDto.getPhone().length() == 0) {
            return true;
        }
        return Validate.validatePhone(registerReqDto.getPhone());
    }
}
