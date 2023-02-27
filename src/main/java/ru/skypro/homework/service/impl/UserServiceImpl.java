package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.UserDto;

import ru.skypro.homework.entity.Avatar;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.AvatarNotFoundException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UsersRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

/**
 * This class handles the command associated with creating user, allowing users to manage with own accounts.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl {
    private final UsersRepository usersRepository;
    private final AvatarServiceImpl avatarService;
    private final UserMapper userMapper;

    public User getDefaultUser() {
        return usersRepository.findByUsernameAndPas("user@gmail.com", "password").orElseThrow(() ->
                new UsernameNotFoundException("Default User"));
    }

    public UserDto getUser() {
        return userMapper.userToDto(getDefaultUser());
    }

    /**
     * This method, used method repository, allows set Password the User
     * Uses {@link UsersRepository#save(Object)}
     * @param body is not null
     * @return body
     */
    public NewPasswordDto setPassword(NewPasswordDto body) {
        User user = getDefaultUser();
        if (user.getPassword().equals(body.getCurrentPassword())) {
            user.setPassword(body.getNewPassword());
            usersRepository.save(user);
        }
        return body;
    }

    /**
     * This method, used method repository, allows update User
     * {@link UsersRepository#save(Object)}
     * @param body is not null
     * @return User
     */
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
        usersRepository.save(oldUser);
        return userMapper.userToDto(oldUser);
    }

    /**
     * This method allows get Name file for avatar
     * @param user is not null
     * @return user
     */
    private String getNameFileForAvatar(User user) {
        return "user_" + user.getId();
    }

    /**
     * This method used method repository allows update user image
     * {@link AvatarServiceImpl#addAvatar(MultipartFile, String)}
     * {@link AvatarServiceImpl#updateAvatar(Avatar, MultipartFile, String)
     * {@link UsersRepository#save(Object)}
     * @param image is not null
     * @return user or HttpStatus.Ok
     * @throws IOException
     */
    public ResponseEntity<Void> updateUserImage(MultipartFile image) throws IOException {
        User user = getDefaultUser();
        if (user.getAvatar() == null) {
            user.setAvatar(avatarService.addAvatar(image, getNameFileForAvatar(user)));
        } else {
            user.setAvatar(avatarService.updateAvatar(user.getAvatar(), image, getNameFileForAvatar(user)));
        }
        usersRepository.save(user);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * This method used method repository allows get avatar data of user
     * {@link AvatarServiceImpl#getAvatarData(Avatar)}
     * @param user is  not null
     * @return avatar data
     */
    private Pair<byte[], String> getAvatarDataOfUser(User user) {
        if (user.getAvatar() != null) {
            return avatarService.getAvatarData(user.getAvatar());
        } else {
            throw new AvatarNotFoundException("User with id = " + user.getId() + "don't have avatar");
        }
    }

    /**
     * This method generates default user
     */
    @PostConstruct
    public void generateDefaultUser() {
        try {
            getDefaultUser();
        } catch (UsernameNotFoundException e) {
            User user = new User();
            user.setId(1);
            user.setEmail("user@gmail.com");
            user.setPhone("0987654321");
            user.setFirstName("Default");
            user.setLastName("User");
            user.setRegDate(LocalDate.now());
            user.setRole(Role.ADMIN);
            user.setUsername("user@gmail.com");
            user.setPassword("password");
            usersRepository.save(user);
        }
    }

    /**
     * This method get random user
     * @return randob user
     */
    public User getRandomUser() {
        List<User> userList = usersRepository.findAll();
        if (userList.size() == 0) {
            return null;
        } else {
            Random random = new Random();
            return userList.get(random.nextInt(userList.size()));
        }
    }

    /**
     * This method used method repository allows get avatar of user
     * {@link UsersRepository#findById(Object)}
     * @param idUser is not null
     * @return user
     */
    public Pair<byte[], String> getAvatarOfUser(Integer idUser) {
        User user = usersRepository.findById(idUser).orElseThrow(() ->
                new UserNotFoundException(idUser));
        return getAvatarDataOfUser(user);
    }

    /**
     * This method get avatar me
     * @return avatar
     */
    public Pair<byte[], String> getAvatarMe() {
        return getAvatarDataOfUser(getDefaultUser());
    }
}
