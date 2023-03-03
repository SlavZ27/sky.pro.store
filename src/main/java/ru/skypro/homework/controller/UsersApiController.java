package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.ResponseWrapperAdsDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.service.impl.UserServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;


@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@Validated
@RequestMapping(value = "users")
public class UsersApiController {
    private final UserServiceImpl userService;

    public UsersApiController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Operation(summary = "getUser", description = "", tags = {"Пользователи"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @GetMapping(value = "me",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserDto> getUser1(Authentication authentication) {
        return ResponseEntity.ok(userService.getUser(authentication.getName()));
    }

    @Operation(summary = "getAvatarOfMe", description = "", tags = {"Пользователи"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = byte[].class)))),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @GetMapping(value = "me/image",
            produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<byte[]> getAvatar(Authentication authentication) {
        Pair<byte[], String> pair = userService.getAvatarMe(authentication.getName());
        return read(pair);
    }

    @Operation(summary = "getAvatarOfUser", description = "", tags = {"Пользователи"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = byte[].class)))),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @GetMapping(value = "{idUser}/image",
            produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<byte[]> getAvatarOfUser(@PathVariable Integer idUser) {
        Pair<byte[], String> pair = userService.getAvatarOfUser(idUser);
        return read(pair);
    }

    @Operation(summary = "setPassword", description = "", tags = {"Пользователи"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = NewPasswordDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @PostMapping(value = "set_password",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<NewPasswordDto> setPassword(NewPasswordDto body) {
        return ResponseEntity.ok(userService.setPassword(body));
    }

    @Operation(summary = "updateUser", description = "", tags = {"Пользователи"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @PatchMapping(value = "me",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserDto> updateUser(
            Authentication authentication,
            @Parameter(in = ParameterIn.DEFAULT, description = "", required = true,
                    schema = @Schema()) @Valid @RequestBody UserDto body) {
        return ResponseEntity.ok(userService.updateUser(authentication.getName(), body));
    }

    @Operation(summary = "updateUserImage", description = "UpdateUserImage", tags = {"Пользователи"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @PatchMapping(value = "/me/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> updateUserImage(
            Authentication authentication,
            MultipartFile image) throws IOException {
        return userService.updateUserImage(authentication.getName(), image);
    }

    private ResponseEntity<byte[]> read(Pair<byte[], String> pair) {
        return ResponseEntity.ok()
                .contentLength(pair.getFirst().length)
                .contentType(MediaType.parseMediaType(pair.getSecond()))
                .body(pair.getFirst());
    }
}
