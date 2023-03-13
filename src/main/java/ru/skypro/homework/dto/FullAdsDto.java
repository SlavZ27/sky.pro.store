package ru.skypro.homework.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * FullAds
 */
@Validated
public class FullAdsDto {
  @JsonProperty("authorFirstName")
  private String authorFirstName;

  @JsonProperty("authorLastName")
  private String authorLastName;

  @JsonProperty("description")
  @NotNull
  private String description;

  @JsonProperty("email")
  private String email;

  @JsonProperty("image")
  private String image;

  @JsonProperty("phone")
  private String phone;

  @JsonProperty("pk")
  private Integer pk;

  @JsonProperty("price")
  @NotNull
  @Positive
  private Integer price;

  @JsonProperty("title")
  @NotNull
  private String title;

  public String getAuthorFirstName() {
    return authorFirstName;
  }

  public void setAuthorFirstName(String authorFirstName) {
    this.authorFirstName = authorFirstName;
  }

  public String getAuthorLastName() {
    return authorLastName;
  }

  public void setAuthorLastName(String authorLastName) {
    this.authorLastName = authorLastName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public Integer getPk() {
    return pk;
  }

  public void setPk(Integer pk) {
    this.pk = pk;
  }

  public Integer getPrice() {
    return price;
  }

  public void setPrice(Integer price) {
    this.price = price;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
