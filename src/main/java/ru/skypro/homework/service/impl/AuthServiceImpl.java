package ru.skypro.homework.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.RegisterReqDto;
import ru.skypro.homework.entity.Role;
import ru.skypro.homework.exception.UserAlreadyExists;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.service.AuthService;

import java.time.LocalDate;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserDetailsManager manager;
    private final PasswordEncoder encoder;
    private final UserServiceImpl userService;
    public final static String PAS_PREFIX = "{bcrypt}";

    public AuthServiceImpl(@Qualifier("jdbcUserDetailsManager") UserDetailsManager manager, UserServiceImpl userService) {
        this.manager = manager;
        this.userService = userService;
        this.encoder = new BCryptPasswordEncoder();
    }

    @Override
    public boolean login(String userName, String password) throws UserNotFoundException{
        if (!manager.userExists(userName)) {
            log.error("Failed authorization attempt. Cause:");
            log.warn("User with userName: {} not found", userName);
            throw new UserNotFoundException(userName);
        }
        UserDetails userDetails = manager.loadUserByUsername(userName);
        String encryptedPassword = userDetails.getPassword();
        String encryptedPasswordWithoutEncryptionType = encryptedPassword.substring(8);
        boolean isLoggedIn = encoder.matches(password, encryptedPasswordWithoutEncryptionType);
        if (isLoggedIn) {
            log.info("User with userName: {} successfully logged in", userName);
        } else {
            log.warn("Failed authorization attempt.  Cause:");
            log.warn("Attempt to enter an incorrect password by userName:{}", userName);
        }
        return isLoggedIn;
    }

    @Override
    public boolean register(RegisterReqDto registerReq) {
        if (manager.userExists(registerReq.getUsername())) {
            log.error("User with userName: {} already exists", registerReq.getUsername());
            throw new UserAlreadyExists(registerReq.getUsername());
        }
        userService.addUser(registerReq, PAS_PREFIX + encoder.encode(registerReq.getPassword()));
        log.info("New user with username: {} has been registered", registerReq.getUsername());
        return true;
    }

    @Override
    public boolean changePassword(NewPasswordDto body) {
        manager.changePassword(
                body.getCurrentPassword(),
                PAS_PREFIX + encoder.encode(body.getNewPassword()));
        UserDetails userDetails =
                manager.loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        String encryptedPassword = userDetails.getPassword();
        String encryptedPasswordWithoutEncryptionType = encryptedPassword.substring(8);
        boolean isChangedPassword = encoder.matches(body.getNewPassword(), encryptedPasswordWithoutEncryptionType);
        if (isChangedPassword) {
            log.info("User with userName: {} has been successfully changed password", userDetails.getUsername());
        } else {
            log.warn("Failed change password attempt. Cause: ");
            log.warn("Attempt to enter an incorrect password by userName:{}", userDetails.getUsername());
        }
        return isChangedPassword;
    }
}
