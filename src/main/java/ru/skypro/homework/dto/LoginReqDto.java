package ru.skypro.homework.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Validated
public class LoginReqDto {
    @JsonProperty("password")
    @NotNull
    private String password;

    @JsonProperty("username")
    @Email
    @NotNull
    private String username;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
