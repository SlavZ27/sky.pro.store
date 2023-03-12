package ru.skypro.homework.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;
import ru.skypro.homework.validate.ValidPhone;

import javax.validation.constraints.Email;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;

@Validated
@ValidPhone
public class UserDto {
    @Email(message = "Email address has invalid format: ${validatedValue}",
            regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
    @JsonProperty("email")
    private String email;
    @JsonProperty("firstName")
    private String firstName;
    @Positive
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("lastName")
    private String lastName;
    @JsonProperty("phone")
    private String phone;
    @PastOrPresent
    @JsonProperty("regDate")
    private String regDate;
    @JsonProperty("image")
    private String image;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
