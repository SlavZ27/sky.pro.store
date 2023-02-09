package ru.skypro.homework.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * AdsBody
 */
@Validated
public class AdsBodyDto {
    @JsonProperty("properties")
    private CreateAdsDto properties;

    public AdsBodyDto properties(CreateAdsDto properties) {
        this.properties = properties;
        return this;
    }

    /**
     * Get properties
     *
     * @return properties
     **/
    @Schema(description = "")
    @NotNull

    @Valid
    public CreateAdsDto getProperties() {
        return properties;
    }

    public void setProperties(CreateAdsDto properties) {
        this.properties = properties;
    }

    @Schema(description = "")
    @NotNull

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdsBodyDto that = (AdsBodyDto) o;
        return Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties);
    }

    @Override
    public String toString() {
        return "AdsBodyDto{" +
                "properties=" + properties +
                '}';
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
