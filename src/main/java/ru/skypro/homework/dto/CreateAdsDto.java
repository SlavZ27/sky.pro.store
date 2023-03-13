package ru.skypro.homework.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.*;

/**
 * CreateAds
 */
@Validated
public class CreateAdsDto {
  @JsonProperty("description")
  @NotNull
  private String description;

  @JsonProperty("price")
  @NotNull
  @Positive
  private Integer price;

  @JsonProperty("title")
  @NotNull
  private String title;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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
