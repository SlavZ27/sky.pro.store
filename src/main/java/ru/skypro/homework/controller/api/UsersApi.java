/**
 * NOTE: This class is auto generated by the swagger code generator program (3.0.40).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package ru.skypro.homework.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UserDto;

import javax.validation.Valid;

@Validated
@RequestMapping(value = "users")
public interface UsersApi {

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
    ResponseEntity<UserDto> getUser1();


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
    ResponseEntity<NewPasswordDto> setPassword(@Parameter(
            in = ParameterIn.DEFAULT, description = "", required = true,
            schema = @Schema(implementation = NewPasswordDto.class)) @Valid @RequestBody NewPasswordDto body);


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
    ResponseEntity<UserDto> updateUser(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true,
            schema = @Schema(implementation = UserDto.class)) @Valid @RequestBody UserDto body);


    @Operation(summary = "updateUserImage", description = "UpdateUserImage", tags = {"Пользователи"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @PatchMapping(value = "/me/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    ResponseEntity<Void> updateUserImage(
            @Parameter(description = "Image of user")
            @Valid @RequestPart("MultipartFile")
            MultipartFile image);
}

