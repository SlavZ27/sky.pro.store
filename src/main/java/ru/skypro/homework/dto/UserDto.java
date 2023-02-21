package ru.skypro.homework.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;

@Validated
public class UserDto {
    @JsonProperty("email")
    private String email;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("regDate")
    private String regDate;

    @JsonProperty("image")
    private String image;

    public UserDto email(String email) {
        this.email = email;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserDto firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public UserDto id(Integer id) {
        this.id = id;
        return this;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserDto lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UserDto phone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserDto regDate(String regDate) {
        this.regDate = regDate;
        return this;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public UserDto image(String image) {
        this.image = image;
        return this;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(email, userDto.email) && Objects.equals(firstName, userDto.firstName) && Objects.equals(id, userDto.id) && Objects.equals(lastName, userDto.lastName) && Objects.equals(phone, userDto.phone) && Objects.equals(regDate, userDto.regDate) && Objects.equals(image, userDto.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, firstName, id, lastName, phone, regDate, image);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class User {\n");

        sb.append("    email: ").append(toIndentedString(email)).append("\n");
        sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
        sb.append("    phone: ").append(toIndentedString(phone)).append("\n");
        sb.append("    regDate: ").append(toIndentedString(regDate)).append("\n");
        sb.append("    image: ").append(toIndentedString(image)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
