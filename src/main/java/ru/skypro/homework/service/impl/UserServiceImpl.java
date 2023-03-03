package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UserDto;

import ru.skypro.homework.entity.Role;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.AvatarNotFoundException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UsersRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
//public class UserServiceImpl implements UserDetailsManager {
public class UserServiceImpl {
    private final UsersRepository usersRepository;
    private final AvatarServiceImpl avatarService;
    private final AuthorityService authorityService;
    private final UserMapper userMapper;


    public UserDto getUser(String username) {
        return userMapper.userToDto(getUserByUserName(username));
    }

    public User getUserByUserName(String userName) {
        return usersRepository.findByUsername(userName).orElseThrow(() ->
                new UserNotFoundException(userName));
    }

    public NewPasswordDto setPassword(NewPasswordDto body) {
        User user = null;
//        User user = getDefaultUser(); /TODO
        if (user.getPassword().equals(body.getCurrentPassword())) {
            user.setPassword(body.getNewPassword());
            usersRepository.save(user);
        }
        return body;
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
        if (newUser.getRegDate() != null) {
            oldUser.setRegDate(newUser.getRegDate());
        }
        usersRepository.save(oldUser);
        return userMapper.userToDto(oldUser);
    }

    private String getNameFileForAvatar(User user) {
        return "user_" + user.getId();
    }

    public ResponseEntity<Void> updateUserImage(String username, MultipartFile image) throws IOException {
        User user = getUserByUserName(username);
        if (user.getAvatar() == null) {
            user.setAvatar(avatarService.addAvatar(image, getNameFileForAvatar(user)));
        } else {
            user.setAvatar(avatarService.updateAvatar(user.getAvatar(), image, getNameFileForAvatar(user)));
        }
        usersRepository.save(user);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    private Pair<byte[], String> getAvatarDataOfUser(User user) {
        if (user.getAvatar() != null) {
            return avatarService.getAvatarData(user.getAvatar());
        } else {
            throw new AvatarNotFoundException("User with id = " + user.getId() + "don't have avatar");
        }
    }

    @PostConstruct
    public void generateDefaultUser() {
        try {
            getUserByUserName("user@gmail.com");
        } catch (UserNotFoundException e) {
            User user = new User();
            user.setEmail("user@gmail.com");
            user.setPhone("0987654321");
            user.setFirstName("user@gmail");
            user.setLastName("User");
            user.setRegDate(LocalDate.now());
            user.setUsername("user@gmail.com");
            user.setPassword("{bcrypt}$2a$12$DEyozL4Gh3JgVyg.wPQFsOMbUxItlqhPafiT.1swhiM870pNhQlCm");
            user.setEnabled(true);
            user = usersRepository.save(user);
            authorityService.addAuthority(user, Role.ROLE_USER);
        }
        try {
            getUserByUserName("adminuser@gmail.com");
        } catch (UserNotFoundException e) {
            User user = new User();
            user.setEmail("adminuser@gmail.com");
            user.setPhone("0987654321");
            user.setFirstName("adminuser@gmail");
            user.setLastName("adminuser");
            user.setRegDate(LocalDate.now());
            user.setUsername("adminuser@gmail.com");
            user.setPassword("{bcrypt}$2a$12$DEyozL4Gh3JgVyg.wPQFsOMbUxItlqhPafiT.1swhiM870pNhQlCm");
            user.setEnabled(true);
            user = usersRepository.save(user);
            authorityService.addAuthority(user, Role.ROLE_ADMIN);
            authorityService.addAuthority(user, Role.ROLE_USER);
        }
        try {
            getUserByUserName("admin@gmail.com");
        } catch (UserNotFoundException e) {
            User user = new User();
            user.setEmail("admin@gmail.com");
            user.setPhone("0987654321");
            user.setFirstName("admin@gmail");
            user.setLastName("admin");
            user.setRegDate(LocalDate.now());
            user.setUsername("admin@gmail.com");
            user.setPassword("{bcrypt}$2a$12$DEyozL4Gh3JgVyg.wPQFsOMbUxItlqhPafiT.1swhiM870pNhQlCm");
            user.setEnabled(true);
            user = usersRepository.save(user);
            authorityService.addAuthority(user, Role.ROLE_ADMIN);
        }
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
