package ru.skypro.homework.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;

@Validated
public class NewPasswordDto {
    @JsonProperty("currentPassword")
    private String currentPassword = null;

    @JsonProperty("newPassword")
    private String newPassword = null;

    public NewPasswordDto currentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
        return this;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public NewPasswordDto newPassword(String newPassword) {
        this.newPassword = newPassword;
        return this;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NewPasswordDto newPassword = (NewPasswordDto) o;
        return Objects.equals(this.currentPassword, newPassword.currentPassword) &&
                Objects.equals(this.newPassword, newPassword.newPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentPassword, newPassword);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class NewPassword {\n");

        sb.append("    currentPassword: ").append(toIndentedString(currentPassword)).append("\n");
        sb.append("    newPassword: ").append(toIndentedString(newPassword)).append("\n");
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
