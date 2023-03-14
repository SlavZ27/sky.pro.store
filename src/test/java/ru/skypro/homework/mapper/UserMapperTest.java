package ru.skypro.homework.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.Avatar;
import ru.skypro.homework.entity.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    public void testUserToUserDto() {
        User user = new User();
        user.setId(111);
        user.setEmail("some@mail.com");
        user.setFirstName("Vasya");
        user.setLastName("Vasin");
        user.setPhone("+79990001122");
        user.setRegDate(LocalDate.parse("11/02/2023", DateTimeFormatter.ofPattern("d/MM/yyyy")));
        Avatar avatar = new Avatar();
        avatar.setId(222);
        avatar.setPath("/some/path/image.png");
        user.setAvatar(avatar);

        UserDto userDto = userMapper.userToDto(user);
        assertEquals(userDto.getId(), 111);
        assertEquals(userDto.getEmail(), "some@mail.com");
        assertEquals(userDto.getFirstName(), "Vasya");
        assertEquals(userDto.getRegDate(), "11/02/2023");
        assertEquals(userDto.getImage(), "/users/111/image");

    }

    @Test
    public void testUserDtoToUser() {
        UserDto userDto = new UserDto();
        userDto.setId(111);
        userDto.setEmail("some@mail.com");
        userDto.setFirstName("Vasya");
        userDto.setLastName("Vasin");
        userDto.setPhone("+79990001122");
        userDto.setRegDate("11/02/2023");
        userDto.setImage("/some/path/image.png");

        User user = userMapper.userDtoToUser(userDto);

        assertEquals(user.getId(), 111);
        assertEquals(user.getEmail(), "some@mail.com");
        assertEquals(user.getFirstName(), "Vasya");
        assertEquals(user.getRegDate(), LocalDate.parse("11/02/2023", DateTimeFormatter.ofPattern("d/MM/yyyy")));
//        assertEquals(user.getAvatar().getPath(), "/some/path/image.png");
    }

}
