package ru.skypro.homework.validate;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.skypro.homework.entity.Role;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ValidatorsTest {
    public ValidPhoneValidator validPhoneValidator = new ValidPhoneValidator();
    public ValidRoleValidator validRoleValidator = new ValidRoleValidator();

    @ParameterizedTest
    @MethodSource("paramForValidPhoneValidatorTest")
    void validPhoneValidatorTest(boolean validActual, String phone) {
        boolean validExpected = validPhoneValidator.isValid(phone, null);
        assertThat(validActual).isEqualTo(validExpected);
    }

    public static Stream<Arguments> paramForValidPhoneValidatorTest() {
        return Stream.of(
                //null
                Arguments.of(true, null),
                //start with +79, length=12
                Arguments.of(true, "+79123789635"),
                //start without +79
                Arguments.of(false, "79123789635"),
                //start without +79 small
                Arguments.of(false, "3789635"),
                //start without +79 long
                Arguments.of(false, "5646546546543789635"),
                //start without +79 long character
                Arguments.of(false, "56465fdghfh3635"),
                //start without +79
                Arguments.of(false, "89123789635")
        );
    }

    @ParameterizedTest
    @MethodSource("paramForValidRoleValidatorTest")
    void validRoleValidatorTest(boolean validActual, String role) {
        boolean validExpected = validRoleValidator.isValid(role, null);
        assertThat(validActual).isEqualTo(validExpected);
    }

    public static Stream<Arguments> paramForValidRoleValidatorTest() {
        return Stream.of(
                Arguments.of(true, null),
                Arguments.of(true, Role.USER.name()),
                Arguments.of(true, Role.ADMIN.name()),
                Arguments.of(false, Role.USER.getRole()),
                Arguments.of(false, Role.ADMIN.getRole()),
                Arguments.of(false, "")
        );
    }
}
