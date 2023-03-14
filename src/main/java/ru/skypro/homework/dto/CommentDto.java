package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Comment
 */
public class CommentDto {
  @JsonProperty("author")
  private Integer author;

  @JsonProperty("createdAt")
  private String createdAt;

  @JsonProperty("pk")
  private Integer pk;

  @JsonProperty("text")
  @NotNull
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
