package ru.skypro.homework.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.*;

import javax.validation.Valid;

@Validated
public interface AuthApi {
    @Operation(summary = "login user", description = "", tags = {"Авторизация"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "*/*",
                    schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @PostMapping(value = "/login",
            produces = {"*/*"},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<Object> login(@Parameter(
            in = ParameterIn.QUERY, description = "Login", required = true, schema = @Schema(
            implementation = LoginReqDto.class))
                                 @Valid @RequestBody LoginReqDto body);

    @Operation(summary = "register user", description = "", tags = {"Авторизация"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @PostMapping(value = "/register",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<Void> register(@Parameter(
            in = ParameterIn.QUERY, description = "register", required = true, schema = @Schema(
            implementation = RegisterReqDto.class))
                                  @Valid @RequestBody RegisterReqDto body);
}
