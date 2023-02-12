package ru.skypro.homework.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;

@Validated
public class RegisterReqDto {
    @JsonProperty("username")
    private String username = null;

    @JsonProperty("password")
    private String password = null;

    @JsonProperty("firstName")
    private String firstName = null;

    @JsonProperty("lastName")
    private String lastName = null;

    @JsonProperty("phone")
    private String phone = null;

    private Role role;


    public RegisterReqDto username(String username) {
        this.username = username;
        return this;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public RegisterReqDto password(String password) {
        this.password = password;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RegisterReqDto firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public RegisterReqDto lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public RegisterReqDto phone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegisterReqDto registerReq = (RegisterReqDto) o;
        return Objects.equals(this.username, registerReq.username) &&
                Objects.equals(this.password, registerReq.password) &&
                Objects.equals(this.firstName, registerReq.firstName) &&
                Objects.equals(this.lastName, registerReq.lastName) &&
                Objects.equals(this.phone, registerReq.phone) &&
                Objects.equals(this.role, registerReq.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, firstName, lastName, phone, role);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RegisterReq {\n");

        sb.append("    username: ").append(toIndentedString(username)).append("\n");
        sb.append("    password: ").append(toIndentedString(password)).append("\n");
        sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
        sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
        sb.append("    phone: ").append(toIndentedString(phone)).append("\n");
        sb.append("    role: ").append(toIndentedString(role)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
