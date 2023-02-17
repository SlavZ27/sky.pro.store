package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.Avatar;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.AvatarNotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UsersRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl {
    private final UsersRepository usersRepository;
    private final AvatarServiceImpl avatarService;
    private final UserMapper userMapper;

    public User getDefaultUser() {
        User user = new User();
        user.setId(1);
        user.setEmail("user@gmail.com");
        user.setPhone("0987654321");
        user.setFirstName("Jack");
        user.setLastName("Black");
        user.setRegDate(LocalDate.now());
        user.setRole(Role.ADMIN);
        user.setPassword("password");
        return user;
    }

    public UserDto getUser() {
        return userMapper.userToDto(getDefaultUser());
    }

    public NewPasswordDto setPassword(NewPasswordDto body) {
        User user = getDefaultUser();
        if (user.getPassword().equals(body.getCurrentPassword())) {
            user.setPassword(body.getNewPassword());
        }
        return body;
    }

    public UserDto updateUser(UserDto body) {
        User newUser = userMapper.userDtoToUser(body);
        User oldUser = getDefaultUser();
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getPhone() != null) {
            oldUser.setPhone(newUser.getPhone());
        }
        if (newUser.getFirstName() != null) {
            oldUser.setFirstName(newUser.getFirstName());
        }
        if (newUser.getLastName() != null) {
            oldUser.setLastName(newUser.getLastName());
        }
        if (newUser.getRegDate() != null) {
            oldUser.setRegDate(newUser.getRegDate());
        }
        return userMapper.userToDto(oldUser);
    }

    public ResponseEntity<Void> updateUserImage(MultipartFile image) throws IOException {
        User user = getDefaultUser();
        user.setAvatar(avatarService.addAvatar(image, user.getId()));
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public Pair<byte[], String> getAvatar() {
        User user = getDefaultUser();
        if (user.getAvatar() != null) {
            return avatarService.getAvatarData(user.getAvatar());
        } else {
            throw new AvatarNotFoundException("");
        }
    }


}
