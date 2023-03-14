package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * ResponseWrapperAds
 */
public class ResponseWrapperAdsDto {
  @JsonProperty("count")
  private Integer count;

  @JsonProperty("results")
  private List<AdsDto> results;

  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public List<AdsDto> getResults() {
    return results;
  }

  public void setResults(List<AdsDto> results) {
    this.results = results;
  }
}
