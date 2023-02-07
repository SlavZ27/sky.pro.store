package ru.skypro.homework.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;

/**
 * ResponseWrapperAds
 */
@Validated
@javax.annotation.Generated(value = "ru.skypro.homeworkcodegen.v3.generators.java.SpringCodegen", date = "2023-02-06T18:24:36.081075022Z[GMT]")


public class ResponseWrapperAds   {
  @JsonProperty("count")
  private Integer count = null;

  @JsonProperty("results")
  @Valid
  private List<Ads> results = null;

  public ResponseWrapperAds count(Integer count) {
    this.count = count;
    return this;
  }

  /**
   * Get count
   * @return count
   **/
  @Schema(description = "")
  
    public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public ResponseWrapperAds results(List<Ads> results) {
    this.results = results;
    return this;
  }

  public ResponseWrapperAds addResultsItem(Ads resultsItem) {
    if (this.results == null) {
      this.results = new ArrayList<Ads>();
    }
    this.results.add(resultsItem);
    return this;
  }

  /**
   * Get results
   * @return results
   **/
  @Schema(description = "")
      @Valid
    public List<Ads> getResults() {
    return results;
  }

  public void setResults(List<Ads> results) {
    this.results = results;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResponseWrapperAds responseWrapperAds = (ResponseWrapperAds) o;
    return Objects.equals(this.count, responseWrapperAds.count) &&
        Objects.equals(this.results, responseWrapperAds.results);
  }

  @Override
  public int hashCode() {
    return Objects.hash(count, results);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResponseWrapperAds {\n");
    
    sb.append("    count: ").append(toIndentedString(count)).append("\n");
    sb.append("    results: ").append(toIndentedString(results)).append("\n");
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
