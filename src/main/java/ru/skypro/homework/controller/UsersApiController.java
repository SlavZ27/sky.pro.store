package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import ru.skypro.homework.controller.api.UsersApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.service.impl.UserServiceImpl;

import java.io.IOException;


@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
//@PreAuthorize("hasAnyAuthority('ADMIN','USER')")
public class UsersApiController implements UsersApi {
    private final UserServiceImpl userService;

    public UsersApiController(UserServiceImpl userService) {
        this.userService = userService;
    }

    public ResponseEntity<UserDto> getUser1() {
        return ResponseEntity.ok(userService.getUser());
    }

    public ResponseEntity<byte[]> getAvatar() {
        Pair<byte[], String> pair =userService.getAvatar();
        return read(pair);
    }

    public ResponseEntity<NewPasswordDto> setPassword(NewPasswordDto body) {
        return ResponseEntity.ok(userService.setPassword(body));
    }

    public ResponseEntity<UserDto> updateUser(UserDto body) {
        return ResponseEntity.ok(userService.updateUser(body));
    }

    public ResponseEntity<Void> updateUserImage(MultipartFile image) throws IOException {
        return userService.updateUserImage(image);
    }
    private ResponseEntity<byte[]> read(Pair<byte[], String> pair) {
        return ResponseEntity.ok()
                .contentLength(pair.getFirst().length)
                .contentType(MediaType.parseMediaType(pair.getSecond()))
                .body(pair.getFirst());
    }
}
