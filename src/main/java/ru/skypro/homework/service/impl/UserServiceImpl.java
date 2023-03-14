package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.RegisterReqDto;
import ru.skypro.homework.dto.UserDto;

import ru.skypro.homework.entity.Authority;
import ru.skypro.homework.entity.Role;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.AvatarNotFoundException;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UsersRepository;
import ru.skypro.homework.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UsersRepository usersRepository;
    private final AuthorityServiceImpl authorityService;
    private final AvatarServiceImpl avatarService;
    private final UserMapper userMapper;

    @Override
    public UserDto getUser(String username) {
        return userMapper.userToDto(getUserByUserName(username));
    }

    @Override
    public User getUserByUserName(String username) {
        return usersRepository.findByUsername(username).orElseThrow(() -> {
            log.error("User with username: {} not found", username);
            throw new UserNotFoundException(username);
        });
    }

    @Override
    public Pair<User, Authority> addUser(RegisterReqDto registerReq, String pass) {
        ru.skypro.homework.entity.User user = userMapper.registerReqToUser(registerReq, pass);
        user.setRegDate(LocalDate.now());
        user.setEnabled(true);
        user = usersRepository.save(user);
        Authority authority = authorityService.addAuthority(user, Role.USER);
        return Pair.of(user, authority);
    }

    @Override
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
        log.info("User with ID: {} has been updated", oldUser.getId());
        return userMapper.userToDto(oldUser);
    }

    @Override
    public String getNameFileForAvatar(User user) {
        return "user_" + user.getId();
    }

    @Override
    public ResponseEntity<Void> updateUserImage(String username, MultipartFile image) throws IOException {
        User user = getUserByUserName(username);
        updateImageOfUser(user, image);
        user = usersRepository.save(user);
        if (user.getAvatar() != null && user.getAvatar().getPath() != null
                && Files.exists(Path.of(user.getAvatar().getPath()))) {
            log.info("User with ID: {} has been updated", user.getId());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void updateImageOfUser(User user, MultipartFile image) throws IOException {
        if (user.getAvatar() == null) {
            user.setAvatar(avatarService.addAvatar(image, getNameFileForAvatar(user)));
            log.info("New avatar has been added for 'user' with ID:{}", user.getId());
        } else {
            user.setAvatar(avatarService.updateAvatar(user.getAvatar(), image, getNameFileForAvatar(user)));
            log.info("Avatar with ID: {} has been updated for 'user' with ID:{}", user.getAvatar().getId(), user.getId());
        }
    }

    @Override
    public Pair<byte[], String> getAvatarDataOfUser(User user) {
        if (user.getAvatar() == null) {
            log.error("An exception occurred! Cause: avatar=null or avatar.Id=null");
            throw new AvatarNotFoundException("User with id = " + user.getId() + "don't have avatar");
        }
        return avatarService.getAvatarData(user.getAvatar());
    }

    @Override
    public Pair<byte[], String> getAvatarOfUser(Integer idUser) {
        User user = usersRepository.findById(idUser).orElseThrow(() -> {
            log.error("User with ID: {} not found", idUser);
            return new UserNotFoundException(idUser);
        });
        return getAvatarDataOfUser(user);
    }

    @Override
    public Pair<byte[], String> getAvatarMe(String username) {
        return getAvatarDataOfUser(getUserByUserName(username));
    }

}
