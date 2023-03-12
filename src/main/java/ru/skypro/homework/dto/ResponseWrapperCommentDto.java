package ru.skypro.homework.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;

/**
 * ResponseWrapperComment
 */
@Validated

public class ResponseWrapperCommentDto {
  @JsonProperty("count")
  private Integer count;

  @JsonProperty("results")
  @Valid
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
