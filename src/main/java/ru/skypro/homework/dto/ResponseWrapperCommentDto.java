package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * ResponseWrapperComment
 */
public class ResponseWrapperCommentDto {
  @JsonProperty("count")
  private Integer count;

  @JsonProperty("results")
  private List<CommentDto> results;

  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public List<CommentDto> getResults() {
    return results;
  }

  public void setResults(List<CommentDto> results) {
    this.results = results;
  }
}
