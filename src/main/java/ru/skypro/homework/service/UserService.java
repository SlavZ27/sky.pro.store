package ru.skypro.homework.service;

import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.RegisterReqDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.Authority;
import ru.skypro.homework.entity.User;

import java.io.IOException;


/**
 * Provides methods for user processing
 */
public interface UserService {
    /**
     * Gets user.
     *
     * @param username the username
     * @return {@link }
     */
    UserDto getUser(String username);

    /**
     * Gets user by username.
     *
     * @param username the username
     * @return {@link User}
     */
    User getUserByUserName(String username);

    /**
     * Add user.
     *
     * @param registerReq the register req
     * @param pass        the pass
     * @return the pair
     */
    Pair<User, Authority> addUser(RegisterReqDto registerReq, String pass);

    /**
     * Update user by username.
     *
     * @param username the username
     * @param body     the body
     * @return {@link UserDto}
     */
    UserDto updateUser(String username, UserDto body);

    /**
     * Gets name file for avatar.
     *
     * @param user the user
     * @return String - the name file for avatar
     */
    String getNameFileForAvatar(User user);

    /**
     * Update user image.
     *
     * @param username the username
     * @param image    the image
     * @return the response entity
     * @throws IOException the io exception
     */
    ResponseEntity<Void> updateUserImage(String username, MultipartFile image) throws IOException;

    /**
     * Update image of user.
     *
     * @param user  the user
     * @param image the image
     * @throws IOException the io exception
     */
    void updateImageOfUser(User user, MultipartFile image) throws IOException;

    /**
     * Gets avatar data of user.
     *
     * @param user the user
     * @return the avatar data of user
     */
    Pair<byte[], String> getAvatarDataOfUser(User user);

    /**
     * Gets avatar of user.
     *
     * @param idUser the id user
     * @return the avatar of user
     */
    Pair<byte[], String> getAvatarOfUser(Integer idUser);

    /**
     * Gets avatar of me-user.
     *
     * @param username the username
     * @return the avatar me
     */
    Pair<byte[], String> getAvatarMe(String username);
}
