package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.UserDto;

import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.AvatarNotFoundException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UsersRepository;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl {
    private final UsersRepository usersRepository;
    private final AvatarServiceImpl avatarService;
    private final UserMapper userMapper;


    public UserDto getUser(String username) {
        return userMapper.userToDto(getUserByUserName(username));
    }

    public User getUserByUserName(String userName) {
        return usersRepository.findByUsername(userName).orElseThrow(() ->
                new UserNotFoundException(userName));
    }

    public UserDto updateUser(String username, UserDto body) {
        User newUser = userMapper.userDtoToUser(body);
        User oldUser = getUserByUserName(username);
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
        oldUser = usersRepository.save(oldUser);
        return userMapper.userToDto(oldUser);
    }

    private String getNameFileForAvatar(User user) {
        return "user_" + user.getId();
    }

    public ResponseEntity<Void> updateUserImage(String username, MultipartFile image) throws IOException {
        User user = getUserByUserName(username);
        updateImageOfUser(user, image);
        usersRepository.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void updateImageOfUser(User user, MultipartFile image) throws IOException {
        if (user.getAvatar() == null) {
            user.setAvatar(avatarService.addAvatar(image, getNameFileForAvatar(user)));
        } else {
            user.setAvatar(avatarService.updateAvatar(user.getAvatar(), image, getNameFileForAvatar(user)));
        }
    }

    private Pair<byte[], String> getAvatarDataOfUser(User user) {
        if (user.getAvatar() == null) {
            throw new AvatarNotFoundException("User with id = " + user.getId() + "don't have avatar");
        }
        return avatarService.getAvatarData(user.getAvatar());
    }



    public Pair<byte[], String> getAvatarOfUser(Integer idUser) {
        User user = usersRepository.findById(idUser).orElseThrow(() ->
                new UserNotFoundException(idUser));
        return getAvatarDataOfUser(user);
    }

    public Pair<byte[], String> getAvatarMe(String username) {
        return getAvatarDataOfUser(getUserByUserName(username));
    }

}
