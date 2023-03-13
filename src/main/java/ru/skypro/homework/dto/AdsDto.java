package ru.skypro.homework.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * Ads
 */
@Validated
public class AdsDto {
  @JsonProperty("author")
  @NotNull
  private Integer author;

  @JsonProperty("image")
  private String image;

  @JsonProperty("pk")
  private Integer pk;

  @JsonProperty("price")
  @NotNull
  @Positive
  private Integer price;

  @JsonProperty("title")
  @NotNull
  private String title;

  public Integer getAuthor() {
    return author;
  }

  public void setAuthor(Integer author) {
    this.author = author;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
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
