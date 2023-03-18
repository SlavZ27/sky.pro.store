package ru.skypro.homework.service;

import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.RegisterReqDto;

/**
 * Provides methods for user login, register and change password
 */
public interface AuthService {
    /**
     * Login.
     *
     * @param userName the username
     * @param password the password
     * @return the boolean
     */
    boolean login(String userName, String password);

    /**
     * Register.
     *
     * @param registerReq the register req
     * @return the boolean
     */
    boolean register(RegisterReqDto registerReq);

    /**
     * Change password.
     *
     * @param body the body
     * @return the boolean
     */
    boolean changePassword(NewPasswordDto body);
}
