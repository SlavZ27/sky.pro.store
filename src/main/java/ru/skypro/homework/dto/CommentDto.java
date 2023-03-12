package ru.skypro.homework.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;

/**
 * Comment
 */
@Validated

public class CommentDto {
  @JsonProperty("author")
  private Integer author;

  @JsonProperty("createdAt")
  private String createdAt;

  @JsonProperty("pk")
  private Integer pk;

  @JsonProperty("text")
  private String text;

  public Integer getAuthor() {
    return author;
  }

  public void setAuthor(Integer author) {
    this.author = author;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public Integer getPk() {
    return pk;
  }

  public void setPk(Integer pk) {
    this.pk = pk;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
