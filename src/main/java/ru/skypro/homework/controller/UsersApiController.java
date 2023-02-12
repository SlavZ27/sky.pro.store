package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import ru.skypro.homework.controller.api.UsersApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UserDto;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class UsersApiController implements UsersApi {
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;


    public ResponseEntity<UserDto> getUser1() {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE)) {
            try {
                return new ResponseEntity<UserDto>(objectMapper.readValue(
                        "{\n" +
                                "  \"firstName\" : \"firstName\",\n  " +
                                "\"lastName\" : \"lastName\",\n  " +
                                "\"image\" : \"image\",\n  " +
                                "\"phone\" : \"phone\",\n  " +
                                "\"city\" : \"city\",\n  " +
                                "\"regDate\" : \"regDate\",\n  " +
                                "\"id\" : 0,\n  " +
                                "\"email\" : \"email\"\n" +
                                "}", UserDto.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<UserDto>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<UserDto>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<NewPasswordDto> setPassword(NewPasswordDto body) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE)) {
            try {
                return new ResponseEntity<NewPasswordDto>(objectMapper.readValue(
                        "{\n  " +
                                "\"newPassword\" : \"newPassword\",\n  " +
                                "\"currentPassword\" : \"currentPassword\"\n" +
                                "}"
                        , NewPasswordDto.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<NewPasswordDto>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<NewPasswordDto>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<UserDto> updateUser(UserDto body) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE)) {
            try {
                return new ResponseEntity<UserDto>(objectMapper.readValue("{\n  \"firstName\" : \"firstName\",\n  \"lastName\" : \"lastName\",\n  \"image\" : \"image\",\n  \"phone\" : \"phone\",\n  \"city\" : \"city\",\n  \"regDate\" : \"regDate\",\n  \"id\" : 0,\n  \"email\" : \"email\"\n}", UserDto.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<UserDto>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<UserDto>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> updateUserImage(MultipartFile image) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }
}
