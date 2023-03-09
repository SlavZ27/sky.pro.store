package ru.skypro.homework.service.impl;

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
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserDetailsManager manager;

    private final PasswordEncoder encoder;
    private final static String PAS_PREFIX = "{bcrypt}";

    public AuthServiceImpl(@Qualifier("jdbcUserDetailsManager") UserDetailsManager manager) {
        this.manager = manager;
        this.encoder = new BCryptPasswordEncoder();
    }

    @Override
    public boolean login(String userName, String password) {
        if (!manager.userExists(userName)) {
            throw new UserNotFoundException(userName);
        }
        UserDetails userDetails = manager.loadUserByUsername(userName);
        String encryptedPassword = userDetails.getPassword();
        String encryptedPasswordWithoutEncryptionType = encryptedPassword.substring(8);
        return encoder.matches(password, encryptedPasswordWithoutEncryptionType);
    }

    @Override
    public boolean register(RegisterReqDto registerReq) {
        if (manager.userExists(registerReq.getUsername())) {
            throw new IllegalArgumentException(registerReq.getUsername());
        }
        manager.createUser(
                User.builder()
                        .password(PAS_PREFIX + encoder.encode(registerReq.getPassword()))
                        .username(registerReq.getUsername())
                        .roles(Role.USER.name())
                        .build()
        );
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
        return encoder.matches(body.getNewPassword(), encryptedPasswordWithoutEncryptionType);
    }
}
