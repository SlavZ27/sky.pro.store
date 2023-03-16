package ru.skypro.homework.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.util.Pair;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.RegisterReqDto;
import ru.skypro.homework.entity.Authority;
import ru.skypro.homework.entity.Role;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.UserAlreadyExists;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.service.AuthService;

/**
 * Provides implementations of AuthService methods
 * @see AuthService
 */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    /**
     * The Manager.
     */
    private final UserDetailsManager manager;
    /**
     * The Encoder.
     */
    private final PasswordEncoder encoder;
    /**
     * The User service.
     */
    private final UserServiceImpl userService;
    /**
     * The constant PAS_PREFIX.
     */
    public final static String PAS_PREFIX = "{bcrypt}";

    /**
     * Instantiates a new Auth service.
     *
     * @param manager     the manager
     * @param userService the user service
     */
    public AuthServiceImpl(@Qualifier("jdbcUserDetailsManager") UserDetailsManager manager, UserServiceImpl userService) {
        this.manager = manager;
        this.userService = userService;
        this.encoder = new BCryptPasswordEncoder();
    }

    /**
     * Login boolean.
     *
     * @param userName the user name
     * @param password the password
     * @return the boolean
     * @throws UserNotFoundException the user not found exception
     */
    @Override
    public boolean login(String userName, String password) throws UserNotFoundException {
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

    /**
     * Register boolean.
     *
     * @param registerReq the register req
     * @return the boolean
     */
    @Override
    public boolean register(RegisterReqDto registerReq) {
        if (manager.userExists(registerReq.getUsername())) {
            log.error("User with userName: {} already exists", registerReq.getUsername());
            throw new UserAlreadyExists(registerReq.getUsername());
        }
        Pair<User, Authority> pair =
                userService.addUser(registerReq, PAS_PREFIX + encoder.encode(registerReq.getPassword()));
        if (pair != null
                && pair.getFirst().getUsername() != null
                && pair.getFirst().getUsername().equals(registerReq.getUsername())
                && pair.getSecond().getUsername() != null
                && pair.getSecond().getUsername().equals(registerReq.getUsername())
                && pair.getSecond().getAuthority() != null
                && pair.getSecond().getAuthority().equals(Role.USER.getRole())) {
            log.info("New user with username: {} has been registered", registerReq.getUsername());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Change password boolean.
     *
     * @param body the body
     * @return the boolean
     */
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
