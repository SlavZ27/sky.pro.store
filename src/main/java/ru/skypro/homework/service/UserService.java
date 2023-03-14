package ru.skypro.homework.service;

import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.RegisterReqDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.Authority;
import ru.skypro.homework.entity.User;

import java.io.IOException;

public interface UserService {
    UserDto getUser(String username);

    User getUserByUserName(String username);

    Pair<User, Authority> addUser(RegisterReqDto registerReq, String pass);

    UserDto updateUser(String username, UserDto body);

    String getNameFileForAvatar(User user);

    ResponseEntity<Void> updateUserImage(String username, MultipartFile image) throws IOException;

    void updateImageOfUser(User user, MultipartFile image) throws IOException;

    Pair<byte[], String> getAvatarDataOfUser(User user);

    Pair<byte[], String> getAvatarOfUser(Integer idUser);

    Pair<byte[], String> getAvatarMe(String username);
}
